package net.darkmeow.irc.client.network;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.kqueue.KQueue;
import io.netty.channel.kqueue.KQueueEventLoopGroup;
import io.netty.channel.kqueue.KQueueSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.proxy.HttpProxyHandler;
import io.netty.handler.proxy.Socks5ProxyHandler;
import net.darkmeow.irc.client.IRCClient;
import net.darkmeow.irc.client.enums.EnumDisconnectType;
import net.darkmeow.irc.client.network.handle.HandleClientConnection;
import net.darkmeow.irc.client.network.handle.HandleClientEncryption;
import net.darkmeow.irc.client.network.handle.HandleClientPacketProcess;
import net.darkmeow.irc.network.PacketUtils;
import net.darkmeow.irc.network.packet.c2s.C2SPacket;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class IRCClientConnection {

    @NotNull
    public IRCClient base;

    public IRCClientConnection(@NotNull IRCClient base) {
        this.base = base;
    }

    @NotNull
    public String key = "";

    public Channel channel;

    @Nullable
    public UUID channelUniqueId;


    @SuppressWarnings("all")
    public boolean connect(@NotNull String host, int port, @NotNull String key, @NotNull Proxy proxy) {
        this.key = key;

        this.channelUniqueId = null;
        this.channel = null;

        try {
            CountDownLatch channelActiveLatch = new CountDownLatch(1);

            Class <? extends SocketChannel > oclass;
            EventLoopGroup group;

            if (Epoll.isAvailable()) {
                group = new EpollEventLoopGroup();
                oclass = EpollSocketChannel.class;
            } else if (KQueue.isAvailable()) {
                group = new KQueueEventLoopGroup();
                oclass = KQueueSocketChannel.class;
            } else {
                group = new NioEventLoopGroup();
                oclass = NioSocketChannel.class;
            }

            new Bootstrap()
                .group(group)
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

                            // 处理
                            ch.pipeline().addLast("BaseConnection", new HandleClientConnection(IRCClientConnection.this, group, channelActiveLatch));
                            ch.pipeline().addLast("BaseEncryption", new HandleClientEncryption(IRCClientConnection.this));
                            ch.pipeline().addLast("Handler", new HandleClientPacketProcess(IRCClientConnection.this));
                        }
                    }
                )
                .channel(oclass)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .option(ChannelOption.SO_REUSEADDR, true)
                .connect(host, port)
                .addListener((ChannelFutureListener) future -> {
                    if (!future.isSuccess()) {
                        future.cause().printStackTrace();
                    }
                });

            channelActiveLatch.await(5, TimeUnit.SECONDS);
            if (channelActiveLatch.getCount() == 1) channelActiveLatch.countDown();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return isConnected();
    }

    @SuppressWarnings("all")
    public boolean isConnected() {
        try {
            return channel != null && channel.isActive();
        } catch (Exception e) {
            e.printStackTrace();

            return false;
        }
    }

    public void disconnect() {
        if (isConnected()) {
            base.resultManager.disconnectType = EnumDisconnectType.DISCONNECT_BY_USER;
            base.resultManager.disconnectReason = null;
            base.resultManager.disconnectLogout = false;

            channel.close().awaitUninterruptibly();
        }
    }

    /**
     * 发送原始消息
     *
     * @param message 消息文本
     * @param async 异步发送
     *
     * @return 是否成功
     */
    @SuppressWarnings("all") // printStackTrace()
    public boolean sendMessage(String message, boolean async) {
        try {
            if (channel.isActive()) {
                if (async) {
                    channel.writeAndFlush(message).addListener((ChannelFutureListener) channelFuture -> {
                        if (!channelFuture.isSuccess()) {
                            channelFuture.cause().printStackTrace();
                        }
                    });
                    return true;
                } else {
                    return channel.writeAndFlush(message).sync().isSuccess();
                }
            } else {
                return false;
            }
        } catch (UnsupportedOperationException ignored) {
            // 连接故障
            return false;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        } catch (Throwable e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 发送 C2SPacket
     *
     * @param packet 网络包
     * @param async 异步发送
     *
     * @return 是否成功
     */
    public boolean sendPacket(C2SPacket packet, boolean async) {
        return this.sendMessage(PacketUtils.generatePacket(packet), async);
    }
}
