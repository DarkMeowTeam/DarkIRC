package net.darkmeow.irc.network;

import io.netty.channel.*;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.Getter;
import net.darkmeow.irc.network.handle.encryption.NettyEncryptingDecoder;
import net.darkmeow.irc.network.handle.encryption.NettyEncryptingEncoder;
import net.darkmeow.irc.network.packet.Packet;
import net.darkmeow.irc.utils.CryptUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.crypto.SecretKey;

public class IRCNetworkManager extends ChannelInboundHandlerAdapter {

    @Getter
    @Nullable
    protected Channel channel;

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        channel = ctx.channel();
        synchronized (this) {
            this.notifyAll();
        }
        setConnectionState(EnumConnectionState.HANDSHAKING);
    }

    /**
     * 是否已建立连接
     *
     * @return 连接状态
     */
    public boolean isConnected() {
        return channel != null && channel.isOpen();
    }

    public void close() {
        if (this.channel != null && this.channel.isOpen())
        {
            this.channel.close().awaitUninterruptibly();
        }
    }

    /**
     * 更新连接状态
     *
     * @param newState 新的
     */
    public void setConnectionState(EnumConnectionState newState) {
        if (this.channel != null) {
            this.channel.attr(IRCNetworkAttributes.PROTOCOL_TYPE).set(newState);
            this.channel.config().setAutoRead(true);
        }
    }

    /**
     * 获取当前连接状态
     *
     * @return 当前连接状态
     */
    @Nullable
    public EnumConnectionState getConnectionState() {
        return this.channel != null ? this.channel.attr(IRCNetworkAttributes.PROTOCOL_TYPE).get() : null;
    }

    public void sendPacket(@NotNull Packet packet) {
        if (this.channel != null && this.channel.isActive()) {
            ChannelFuture channelfuture = this.channel.writeAndFlush(packet);

            channelfuture.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
        }
    }

    @SafeVarargs
    public final void sendPacket(@NotNull Packet packet, @NotNull GenericFutureListener<? extends Future<? super Void>>... futureListeners) {
        if (this.channel != null && this.channel.isActive()) {
            ChannelFuture channelfuture = this.channel.writeAndFlush(packet);

            if (futureListeners != null)
            {
                channelfuture.addListeners(futureListeners);
            }
            channelfuture.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
        }
    }

    /**
     * 连接是否已启用加密
     */
    @Getter
    private boolean isEncrypted = false;

    /**
     * 开启连接加密功能
     *
     * @param key 密钥
     */
    public void enableEncryption(SecretKey key) {
        if (this.channel != null) {
            this.channel.pipeline().addBefore("splitter", "decrypt", new NettyEncryptingDecoder(CryptUtils.createNetCipherInstance(2, key)));
            this.channel.pipeline().addBefore("prepender", "encrypt", new NettyEncryptingEncoder(CryptUtils.createNetCipherInstance(1, key)));

            this.isEncrypted = true;
        }
    }
}
