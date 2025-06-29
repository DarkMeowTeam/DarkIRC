package net.darkmeow.irc.client.network;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.TimeoutException;
import net.darkmeow.irc.IRCLib;
import net.darkmeow.irc.client.IRCClient;
import net.darkmeow.irc.client.enums.EnumDisconnectType;
import net.darkmeow.irc.client.network.handle.handshake.*;
import net.darkmeow.irc.client.network.handle.login.HandleLoginBase;
import net.darkmeow.irc.client.network.handle.online.*;
import net.darkmeow.irc.client.options.proxy.IRCOptionsProxy;
import net.darkmeow.irc.network.EnumPacketDirection;
import net.darkmeow.irc.network.IRCNetworkBaseConfig;
import net.darkmeow.irc.network.IRCNetworkManager;
import net.darkmeow.irc.network.handle.frame.NettyVarInt21FrameDecoder;
import net.darkmeow.irc.network.handle.frame.NettyVarInt21FrameEncoder;
import net.darkmeow.irc.network.handle.packet.NettyPacketDecoder;
import net.darkmeow.irc.network.handle.packet.NettyPacketEncoder;
import net.darkmeow.irc.network.packet.handshake.c2s.C2SPacketHandShake;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class IRCClientNetworkManager extends IRCNetworkManager {

    public static MultiThreadIoEventLoopGroup eventLoopGroup = new MultiThreadIoEventLoopGroup(NioIoHandler.newFactory());

    public static IRCClientNetworkManager createNetworkManagerAndConnect(@NotNull IRCClient base, @NotNull String host, int port, @Nullable IRCOptionsProxy proxy) throws Throwable {
        final IRCClientNetworkManager networkManager = new IRCClientNetworkManager(base);

        ChannelFuture future = new Bootstrap()
            .group(eventLoopGroup)
            .handler(
                new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        // 代理
                        if (proxy != null) {
                            final ChannelHandler handler = proxy.getNettyHandler();
                            if (handler != null) {
                                ch.pipeline().addLast("proxy", handler);
                            }
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
                        ch.pipeline().addLast("base", networkManager);

                        ch.pipeline().addLast("handler_handshake_base", new HandleHandShakeBase(networkManager));
                        ch.pipeline().addLast("handler_handshake_compression", new HandleHandShakeCompression(networkManager));
                        ch.pipeline().addLast("handler_handshake_encryption", new HandleHandShakeEncryption(networkManager));
                        ch.pipeline().addLast("handler_handshake_server_redirect", new HandleHandShakeServerRedirect(networkManager));
                        ch.pipeline().addLast("handler_handshake_server_info", new HandleHandShakeServerInfo(networkManager));

                        ch.pipeline().addLast("handler_login_base", new HandleLoginBase(networkManager));

                        ch.pipeline().addLast("handler_online_keepalive", new HandleOnlineKeepAlive(networkManager));
                        ch.pipeline().addLast("handler_online_update_my_profile", new HandleOnlineUpdateMyProfile(networkManager));
                        ch.pipeline().addLast("handler_online_message", new HandleOnlineMessage(networkManager));
                        ch.pipeline().addLast("handler_online_input_status", new HandleOnlineInputStatus(networkManager));
                        ch.pipeline().addLast("handler_online_session_status", new HandleOnlineSessionStatus(networkManager));
                        ch.pipeline().addLast("handler_online_session_skin", new HandleOnlineSessionSkin(networkManager));
                        ch.pipeline().addLast("handler_online_remote_disconnect", new HandleOnlineRemoteDisconnect(networkManager));
                        ch.pipeline().addLast("handler_online_custom_payload", new HandleOnlineCustomPayload(networkManager));
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
        sendPacket(new C2SPacketHandShake(IRCLib.PROTOCOL_VERSION, base.options.host, base.options.port, base.options.hardWareUniqueId, base.options.brand, System.currentTimeMillis()));
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
