package net.darkmeow.irc.client.network.handle.handshake;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.darkmeow.irc.client.enums.EnumDisconnectType;
import net.darkmeow.irc.client.network.IRCClientNetworkManager;
import net.darkmeow.irc.client.network.IRCClientRemoteVerify;
import net.darkmeow.irc.network.packet.handshake.s2c.S2CPacketServerInfo;
import org.jetbrains.annotations.NotNull;

public final class HandleHandShakeServerInfo extends SimpleChannelInboundHandler<S2CPacketServerInfo> {

    @NotNull
    public final IRCClientNetworkManager connection;

    public HandleHandShakeServerInfo(@NotNull IRCClientNetworkManager connection) {
        this.connection = connection;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);

        final IRCClientRemoteVerify remoteVerify = connection.base.options.remoteVerify;

        if (remoteVerify != null) {
            remoteVerify.verify = false;
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, S2CPacketServerInfo packet) throws Exception {
        final IRCClientRemoteVerify remoteVerify = connection.base.options.remoteVerify;

        if (remoteVerify != null) {
            if (!packet.checkSignature(remoteVerify.getKey())) {
                this.connection.base.closeChannel(EnumDisconnectType.FAILED_TO_LOGIN, "服务器身份验证失败 (无效签名)", false);
                return;
            }
            final long currentTime = System.currentTimeMillis();
            if (currentTime - 15000L > packet.getTimestamp() || packet.getTimestamp() > currentTime + 15000L) {
                this.connection.base.closeChannel(EnumDisconnectType.FAILED_TO_LOGIN, "服务器身份验证失败 (校时偏差过大)", false);
                return;
            }
            remoteVerify.verify = true;
        }
    }

}
