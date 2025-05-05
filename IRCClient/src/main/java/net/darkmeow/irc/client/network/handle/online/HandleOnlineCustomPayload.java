package net.darkmeow.irc.client.network.handle.online;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.darkmeow.irc.client.network.IRCClientNetworkManager;
import net.darkmeow.irc.network.packet.online.s2c.S2CPacketCustomPayload;
import org.jetbrains.annotations.NotNull;

public final class HandleOnlineCustomPayload extends SimpleChannelInboundHandler<S2CPacketCustomPayload> {

    @NotNull
    public final IRCClientNetworkManager connection;

    public HandleOnlineCustomPayload(@NotNull IRCClientNetworkManager connection) {
        this.connection = connection;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, S2CPacketCustomPayload packet) {
        connection.base.listenable.onCustomPayload(packet.getChannel(), packet.getData());
    }

}
