package net.darkmeow.irc.client.network.handle.handshake;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.darkmeow.irc.client.network.IRCClientNetworkManager;
import net.darkmeow.irc.network.packet.handshake.s2c.S2CPacketEnableCompression;
import org.jetbrains.annotations.NotNull;

public final class HandleHandShakeCompression extends SimpleChannelInboundHandler<S2CPacketEnableCompression> {

    @NotNull
    public final IRCClientNetworkManager connection;

    public HandleHandShakeCompression(@NotNull IRCClientNetworkManager connection) {
        this.connection = connection;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, S2CPacketEnableCompression packet) throws Exception {
        connection.enableCompression(packet.getThreshold());
    }

}
