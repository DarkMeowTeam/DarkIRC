package net.darkmeow.irc.client.network.handle.online;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.darkmeow.irc.client.data.DataOtherSessionInfo;
import net.darkmeow.irc.client.network.IRCClientNetworkManager;
import net.darkmeow.irc.network.packet.online.s2c.S2CPacketUpdateSkin;
import org.jetbrains.annotations.NotNull;

public final class HandleProcessClientOnlineSessionSkin extends SimpleChannelInboundHandler<S2CPacketUpdateSkin> {

    @NotNull
    public final IRCClientNetworkManager connection;

    public HandleProcessClientOnlineSessionSkin(@NotNull IRCClientNetworkManager connection) {
        this.connection = connection;
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, S2CPacketUpdateSkin packet) {
        final DataOtherSessionInfo info = connection.base.sessionManager.users.get(packet.getSessionId());
        if (info != null) {
            info.update(packet.getSkin());
            connection.base.listenable.onUpdateSessionSkin(info);
        }
    }

}
