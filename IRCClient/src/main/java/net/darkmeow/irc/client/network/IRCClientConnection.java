package net.darkmeow.irc.client.network;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.proxy.HttpProxyHandler;
import io.netty.handler.proxy.Socks5ProxyHandler;
import net.darkmeow.irc.client.IRCClient;
import net.darkmeow.irc.client.data.IRCOtherUserInfo;
import net.darkmeow.irc.client.network.handle.HandleClientEncryption;
import net.darkmeow.irc.client.network.handle.HandleClientPacketProcess;
import net.darkmeow.irc.network.PacketUtils;
import net.darkmeow.irc.network.packet.c2s.C2SPacket;

import java.net.Proxy;
import java.util.HashMap;
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
            } catch (Exception e) {
                group.shutdownGracefully();
                latch.countDown();
            }
        }).start();

        try { latch.await(); } catch (InterruptedException ignored) { }

        return isConnected();
    }

    public boolean isConnected() {
        return channel != null && channel.isActive();
    }

    public void disconnect() {
        if (isConnected()) {
            channel.close();
        }
    }

    public void sendMessage(String message) {
        channel.writeAndFlush(message);
    }

    public void sendPacket(C2SPacket packet) {
        this.sendMessage(PacketUtils.generatePacket(packet));
    }
}
