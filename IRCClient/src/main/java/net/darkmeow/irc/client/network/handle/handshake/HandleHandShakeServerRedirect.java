package net.darkmeow.irc.client.network.handle.handshake;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.darkmeow.irc.client.enums.EnumDisconnectType;
import net.darkmeow.irc.client.network.IRCClientNetworkManager;
import net.darkmeow.irc.network.packet.handshake.s2c.S2CPacketRedirectServer;
import org.jetbrains.annotations.NotNull;

public final class HandleHandShakeServerRedirect extends SimpleChannelInboundHandler<S2CPacketRedirectServer> {

    @NotNull
    public final IRCClientNetworkManager connection;

    public HandleHandShakeServerRedirect(@NotNull IRCClientNetworkManager connection) {
        this.connection = connection;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, S2CPacketRedirectServer packet) {
        this.connection.base.options.host = packet.getHost();
        this.connection.base.options.port = packet.getPort();

        this.connection.base.closeChannel(EnumDisconnectType.KICK_BY_SERVER, "服务器地址更新, 请重新连接", false);
    }

}
