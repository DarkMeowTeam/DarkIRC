package net.darkmeow.irc.network.handles.online

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import net.darkmeow.irc.network.IRCNetworkManagerServer
import net.darkmeow.irc.network.packet.C2SPacket
import net.darkmeow.irc.network.packet.online.c2s.C2SPacketLogout
import net.darkmeow.irc.network.packet.online.c2s.C2SPacketUpdatePassword

class HandlePacketAuthentication(private val connection: IRCNetworkManagerServer): SimpleChannelInboundHandler<C2SPacket>() {

    override fun channelRead0(ctx: ChannelHandlerContext, packet: C2SPacket) {
        when(packet) {
            is C2SPacketLogout -> {
                connection.currentToken
                    ?.takeIf { packet.isDestroySessionKey }
                    ?.also { token -> connection.bossNetworkManager.base.dataManager.deleteSession(token) }

                connection.kick(reason = "您已退出登录", logout = packet.isDestroySessionKey)
            }
            is C2SPacketUpdatePassword -> {
                connection.bossNetworkManager.base.dataManager.deleteSessionByUser(connection.user)
                connection.bossNetworkManager.base.dataManager.setUserPassword(connection.user, packet.password)

                connection.bossNetworkManager.clients.values
                    .filter { client -> client.user == connection.user }
                    .forEach { client -> client.kick(reason = "账号密码已修改, 请重新登录", logout = true) }
                }
            else -> ctx.fireChannelRead(packet)
        }
    }

}