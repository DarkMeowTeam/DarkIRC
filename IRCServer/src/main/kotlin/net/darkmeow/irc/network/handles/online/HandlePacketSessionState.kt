package net.darkmeow.irc.network.handles.online

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import net.darkmeow.irc.data.DataUser
import net.darkmeow.irc.network.IRCNetworkManagerServer
import net.darkmeow.irc.network.packet.C2SPacket
import net.darkmeow.irc.network.packet.online.c2s.C2SPacketQuerySessions
import net.darkmeow.irc.network.packet.online.c2s.C2SPacketUploadState
import net.darkmeow.irc.network.packet.online.s2c.S2CPacketUpdateSessionState
import net.darkmeow.irc.network.packet.online.s2c.S2CPacketUpdateSessionStateMulti
import net.darkmeow.irc.utils.QuickBoardCastUtils.sendPacketToAllIgnoreInvisible

class HandlePacketSessionState(private val connection: IRCNetworkManagerServer): SimpleChannelInboundHandler<C2SPacket>() {

    override fun channelRead0(ctx: ChannelHandlerContext, packet: C2SPacket) {
        when(packet) {
            is C2SPacketQuerySessions -> {
                connection.bossNetworkManager.clients.values
                    // 隐身会话不发送
                    .filter { other -> !other.currentIsInvisible || other.user == connection.user }
                    .filter { other ->
                        if (packet.isOnlySameServer) {
                            other.sessionState.currentServer.equals(other = other.sessionState.currentServer, ignoreCase = true)
                        } else {
                            true
                        }
                    }
                    .associate { other ->
                        Pair(other.sessionId, DataUser(other.user, connection.userPremium, other.sessionState))
                    }
                    .toMap()
                    .also { map ->
                        // 告诉客户端这个 id 是 irc 内用户
                        connection.sendPacket(S2CPacketUpdateSessionStateMulti(packet.isOnlySameServer, true, map))
                    }
            }
            is C2SPacketUploadState -> {
                if (!packet.state.currentServer.equals(connection.sessionState.currentServer, ignoreCase = true) || !packet.state.profile.name.equals(connection.sessionState.profile.name, ignoreCase = true)) {
                    connection.bossNetworkManager.base.logger.info("[${connection.user}] 游戏状态更新: ${packet.state.profile.name} ${packet.state.currentServer}")
                }

                connection.sessionState = packet.state
                connection.sendPacketToAllIgnoreInvisible(S2CPacketUpdateSessionState(connection.sessionId,DataUser(connection.user, connection.userPremium, connection.sessionState)))
            }
            else -> ctx.fireChannelRead(packet)
        }
    }

    override fun channelInactive(ctx: ChannelHandlerContext) {
        if (connection.isLogin()) {
            connection.sendPacketToAllIgnoreInvisible(S2CPacketUpdateSessionState(connection.sessionId))
        }

        super.channelInactive(ctx)
    }
}