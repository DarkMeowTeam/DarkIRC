package net.darkmeow.irc.network.handles.online

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import net.darkmeow.irc.network.IRCNetworkManagerServer
import net.darkmeow.irc.network.packet.online.c2s.C2SPacketLogout
import net.darkmeow.irc.network.packet.online.s2c.S2CPacketUpdateSessionState

class HandlePacketDisconnect(private val connection: IRCNetworkManagerServer): SimpleChannelInboundHandler<C2SPacketLogout>() {

    override fun channelRead0(ctx: ChannelHandlerContext, packet: C2SPacketLogout) {
        if (packet.isDestroySessionKey) connection.currentToken?.also { token -> connection.bossNetworkManager.base.dataManager.deleteSession(token)}

        connection.disconnect(reason = "您已退出登录", logout = packet.isDestroySessionKey)
    }

    override fun channelInactive(ctx: ChannelHandlerContext) {
        super.channelInactive(ctx)

        connection.bossNetworkManager.clients.values
            // 隐身会话不发送
            .filter { other -> !connection.currentIsInvisible || other.user == connection.user }
            .forEach { other ->
                other.sendPacket(S2CPacketUpdateSessionState(connection.sessionId))
            }
    }
}