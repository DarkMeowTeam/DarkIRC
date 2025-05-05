package net.darkmeow.irc.client.network.handle.online;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.darkmeow.irc.client.enums.EnumDisconnectType;
import net.darkmeow.irc.client.network.IRCClientNetworkManager;
import net.darkmeow.irc.network.packet.online.s2c.S2CPacketDisconnect;
import org.jetbrains.annotations.NotNull;

public final class HandleProcessClientOnlineRemoteDisconnect extends SimpleChannelInboundHandler<S2CPacketDisconnect> {

    @NotNull
    public final IRCClientNetworkManager connection;

    public HandleProcessClientOnlineRemoteDisconnect(@NotNull IRCClientNetworkManager connection) {
        this.connection = connection;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, S2CPacketDisconnect packet) {
        this.connection.base.closeChannel(EnumDisconnectType.KICK_BY_SERVER, packet.getMessage(), packet.isMarkSessionTokenInvalid());
    }

}
