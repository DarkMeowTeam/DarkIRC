package net.darkmeow.irc.client.network;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.proxy.HttpProxyHandler;
import io.netty.handler.proxy.Socks5ProxyHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.TimeoutException;
import net.darkmeow.irc.IRCLib;
import net.darkmeow.irc.client.IRCClient;
import net.darkmeow.irc.client.enums.EnumDisconnectType;
import net.darkmeow.irc.client.network.handle.handshake.HandleProcessClientHandShake;
import net.darkmeow.irc.client.network.handle.handshake.HandleProcessClientSignatureRequest;
import net.darkmeow.irc.client.network.handle.login.HandleProcessClientLogin;
import net.darkmeow.irc.client.network.handle.online.*;
import net.darkmeow.irc.network.EnumPacketDirection;
import net.darkmeow.irc.network.IRCNetworkBaseConfig;
import net.darkmeow.irc.network.IRCNetworkManager;
import net.darkmeow.irc.network.handle.frame.NettyVarInt21FrameDecoder;
import net.darkmeow.irc.network.handle.frame.NettyVarInt21FrameEncoder;
import net.darkmeow.irc.network.handle.packet.NettyPacketDecoder;
import net.darkmeow.irc.network.handle.packet.NettyPacketEncoder;
import net.darkmeow.irc.network.packet.handshake.c2s.C2SPacketHandShake;
import org.jetbrains.annotations.NotNull;

import java.net.Proxy;
import java.util.concurrent.CompletableFuture;

public class IRCClientNetworkManager extends IRCNetworkManager {

    public static NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup();

    public static IRCClientNetworkManager createNetworkManagerAndConnect(@NotNull IRCClient base, @NotNull String host, int port, @NotNull Proxy proxy) throws Throwable {
        final IRCClientNetworkManager networkManager = new IRCClientNetworkManager(base);

        ChannelFuture future = new Bootstrap()
            .group(eventLoopGroup)
            .handler(
                new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        // 代理
                        if (proxy.type() == Proxy.Type.SOCKS) {
                            ch.pipeline().addLast("Proxy", new Socks5ProxyHandler(proxy.address()));
                        } else if (proxy.type() == Proxy.Type.HTTP) {
                            ch.pipeline().addLast("Proxy", new HttpProxyHandler(proxy.address()));
                        }

                        // 超时断开
                        ch.pipeline().addLast("timeout", new ReadTimeoutHandler(IRCNetworkBaseConfig.READ_TIMEOUT_SECOND));
                        // 入站包分片和解码
                        ch.pipeline().addLast("splitter", new NettyVarInt21FrameDecoder());
                        ch.pipeline().addLast("decoder", new NettyPacketDecoder(EnumPacketDirection.CLIENT_BOUND));
                        // 出站包分片和编码
                        ch.pipeline().addLast("prepender", new NettyVarInt21FrameEncoder());
                        ch.pipeline().addLast("encoder", new NettyPacketEncoder(EnumPacketDirection.SERVER_BOUND));

                        // 处理
                        ch.pipeline().addLast("BaseHandler", networkManager);

                        ch.pipeline().addLast("handler_hand_shake", new HandleProcessClientHandShake(networkManager));
                        ch.pipeline().addLast("handler_signature_request", new HandleProcessClientSignatureRequest(networkManager));
                        ch.pipeline().addLast("handler_login", new HandleProcessClientLogin(networkManager));
                        ch.pipeline().addLast("handler_online_keepalive", new HandleProcessClientOnlineKeepAlive(networkManager));
                        ch.pipeline().addLast("handler_online_update_my_profile", new HandleProcessClientOnlineUpdateMyProfile(networkManager));
                        ch.pipeline().addLast("handler_online_message", new HandleProcessClientOnlineMessage(networkManager));
                        ch.pipeline().addLast("handler_online_input_status", new HandleProcessClientOnlineInputStatus(networkManager));
                        ch.pipeline().addLast("handler_online_other_user_data", new HandleProcessClientOnlineOtherSession(networkManager));
                        ch.pipeline().addLast("handler_online_remote_disconnect", new HandleProcessClientOnlineRemoteDisconnect(networkManager));
                    }
                }
            )
            .channel(NioSocketChannel.class)
            .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
            .option(ChannelOption.SO_REUSEADDR, true)
            .connect(host, port)
            .syncUninterruptibly();

        if (future.isSuccess()) {
            synchronized (networkManager) {
                while (networkManager.channel == null) {
                    networkManager.wait();
                }
            }
        } else {
            throw future.cause();
        }

        return networkManager;
    }

    @NotNull
    public IRCClient base;

    public IRCClientNetworkManager(@NotNull IRCClient base) {
        this.base = base;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx)
    {
        super.channelActive(ctx);
        sendPacket(new C2SPacketHandShake(IRCLib.PROTOCOL_VERSION, base.options.host, base.options.port, base.options.deviceId, base.options.brand));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable e)
    {
        if (e instanceof TimeoutException) {
            this.base.closeChannel(EnumDisconnectType.OTHER, "连接超时.", false);
        } else {
            this.base.closeChannel(EnumDisconnectType.OTHER, "内部错误: " + e.getClass().getSimpleName() + ": " + e.getMessage(), false);
        }
    }
}
