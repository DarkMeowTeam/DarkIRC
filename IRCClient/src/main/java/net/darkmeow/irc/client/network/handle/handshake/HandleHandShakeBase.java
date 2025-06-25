package net.darkmeow.irc.client.network.handle.handshake;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.darkmeow.irc.client.enums.EnumDisconnectType;
import net.darkmeow.irc.client.network.IRCClientNetworkManager;
import net.darkmeow.irc.client.network.IRCClientRemoteVerify;
import net.darkmeow.irc.network.EnumConnectionState;
import net.darkmeow.irc.network.packet.S2CPacket;
import net.darkmeow.irc.network.packet.handshake.s2c.S2CPacketDenyHandShake;
import net.darkmeow.irc.network.packet.handshake.s2c.S2CPacketHandShakeSuccess;
import org.jetbrains.annotations.NotNull;

public final class HandleHandShakeBase extends SimpleChannelInboundHandler<S2CPacket> {

    @NotNull
    public final IRCClientNetworkManager connection;

    public HandleHandShakeBase(@NotNull IRCClientNetworkManager connection) {
        this.connection = connection;
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, S2CPacket packet) throws Exception {
        if (packet instanceof S2CPacketHandShakeSuccess) {
            handleHandShakeSuccess((S2CPacketHandShakeSuccess) packet);
        } else if (packet instanceof S2CPacketDenyHandShake) {
            handleDenyHandShake((S2CPacketDenyHandShake) packet);
        } else {
            ctx.fireChannelRead(packet);
        }
    }

    public void handleHandShakeSuccess(S2CPacketHandShakeSuccess packet) {
        final IRCClientRemoteVerify remoteVerify = connection.base.options.remoteVerify;

        if (remoteVerify != null && !remoteVerify.verify) {
            this.connection.base.closeChannel(EnumDisconnectType.FAILED_TO_LOGIN, "服务器身份验证失败 (未发送过签名数据)", false);
            return;
        }

        this.connection.base.sessionManager.reset(packet.getSessionId());
        this.connection.setConnectionState(EnumConnectionState.LOGIN);
        this.connection.base.listenable.onReadyLogin(this.connection.base);
    }

    public void handleDenyHandShake(S2CPacketDenyHandShake packet) {
        this.connection.base.closeChannel(EnumDisconnectType.FAILED_TO_LOGIN, packet.getReason(), false);
    }
}
