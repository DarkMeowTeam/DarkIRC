package net.darkmeow.irc.client.network;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.proxy.HttpProxyHandler;
import io.netty.handler.proxy.Socks5ProxyHandler;
import net.darkmeow.irc.client.IRCClient;
import net.darkmeow.irc.client.network.handle.HandleClientEncryption;
import net.darkmeow.irc.client.network.handle.HandleClientPacketProcess;
import net.darkmeow.irc.network.PacketUtils;
import net.darkmeow.irc.network.packet.c2s.C2SPacket;

import java.net.Proxy;
import java.util.concurrent.CountDownLatch;

public class IRCClientConnection {

    public IRCClient base;

    public IRCClientConnection(IRCClient base) {
        this.base = base;
    }

    public String key;
    public Channel channel;

    public boolean connect(String host, int port, String key) {
        return this.connect(host, port, key, Proxy.NO_PROXY);
    }

    @SuppressWarnings("all")
    public boolean connect(String host, int port, String key, Proxy proxy) {
        this.key = key;

        final CountDownLatch latch = new CountDownLatch(1);

        new Thread(() -> {
            EventLoopGroup group = new NioEventLoopGroup();

            try {
                Bootstrap bootstrap = new Bootstrap();
                bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel ch) {
                            // 代理
                            if (proxy.type() == Proxy.Type.SOCKS) {
                                ch.pipeline().addLast("Proxy", new Socks5ProxyHandler(proxy.address()));
                            } else if (proxy.type() == Proxy.Type.HTTP) {
                                ch.pipeline().addLast("Proxy", new HttpProxyHandler(proxy.address()));
                            }

                            // 处理
                            ch.pipeline().addLast("BaseEncryption", new HandleClientEncryption(IRCClientConnection.this));
                            ch.pipeline().addLast("Handler", new HandleClientPacketProcess(IRCClientConnection.this));
                        }
                    });
                ChannelFuture future = bootstrap.connect(host, port).sync();
                channel = future.channel();
                latch.countDown();

                channel.closeFuture().sync();

                base.listenable.onDisconnect(base.resultManager.disconnectReason);
            } catch (Exception e) {
                e.printStackTrace();

                group.shutdownGracefully();
                latch.countDown();
            }
        }).start();

        try { latch.await(); } catch (InterruptedException ignored) { }

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

    @SuppressWarnings("all")
    public void disconnect() {
        if (isConnected()) {
            try {
                channel.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
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
