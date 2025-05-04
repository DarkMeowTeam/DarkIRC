package net.darkmeow.irc.client.network.handle.online;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.darkmeow.irc.client.network.IRCClientNetworkManager;
import net.darkmeow.irc.network.packet.online.s2c.S2CPacketOtherInputState;
import org.jetbrains.annotations.NotNull;

public final class HandleProcessClientOnlineInputStatus extends SimpleChannelInboundHandler<S2CPacketOtherInputState> {

    @NotNull
    public final IRCClientNetworkManager connection;

    public HandleProcessClientOnlineInputStatus(@NotNull IRCClientNetworkManager connection) {
        this.connection = connection;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, S2CPacketOtherInputState packet) {
        connection.base.listenable.onUpdateOtherInputs(packet.getPublicInputs(), packet.getPrivateInputs());
    }

}
