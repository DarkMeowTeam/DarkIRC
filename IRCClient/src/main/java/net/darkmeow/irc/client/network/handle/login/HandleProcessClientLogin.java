package net.darkmeow.irc.client.network.handle.login;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.darkmeow.irc.client.enums.EnumDisconnectType;
import net.darkmeow.irc.client.network.IRCClientNetworkManager;
import net.darkmeow.irc.network.EnumConnectionState;
import net.darkmeow.irc.network.packet.login.s2c.S2CPacketLoginFailed;
import net.darkmeow.irc.network.packet.login.s2c.S2CPacketLoginSuccess;
import org.jetbrains.annotations.NotNull;

public final class HandleProcessClientLogin extends ChannelInboundHandlerAdapter {

    @NotNull
    public final IRCClientNetworkManager connection;

    public HandleProcessClientLogin(@NotNull IRCClientNetworkManager connection) {
        this.connection = connection;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object packet) throws Exception {
        if (packet instanceof S2CPacketLoginSuccess) {
            handleLoginSuccess((S2CPacketLoginSuccess) packet);
        } else if (packet instanceof S2CPacketLoginFailed) {
            handleLoginFailed((S2CPacketLoginFailed) packet);
        } else {
            super.channelRead(ctx, packet);
        }
    }

    public void handleLoginSuccess(S2CPacketLoginSuccess packet) {
        this.connection.setConnectionState(EnumConnectionState.ONLINE);
        this.connection.base.listenable.onUpdateSession(packet.getToken());
    }

    public void handleLoginFailed(S2CPacketLoginFailed packet) {
        this.connection.base.closeChannel(EnumDisconnectType.KICK_BY_SERVER, packet.getReason(), packet.isMarkSessionTokenInvalid());
    }
}
