package net.darkmeow.irc.client.network.handle.online;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.darkmeow.irc.client.network.IRCClientNetworkManager;
import net.darkmeow.irc.network.packet.online.c2s.C2SPacketKeepAlive;
import net.darkmeow.irc.network.packet.online.s2c.S2CPacketKeepAlive;
import org.jetbrains.annotations.NotNull;

public final class HandleProcessClientOnlineKeepAlive extends SimpleChannelInboundHandler<S2CPacketKeepAlive> {

    @NotNull
    public final IRCClientNetworkManager connection;

    public HandleProcessClientOnlineKeepAlive(@NotNull IRCClientNetworkManager connection) {
        this.connection = connection;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, S2CPacketKeepAlive packet) {
        connection.sendPacket(new C2SPacketKeepAlive(packet.getId()));
    }

}
