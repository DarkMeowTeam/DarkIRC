package net.darkmeow.irc.network.handles.online

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import net.darkmeow.irc.data.DataUser
import net.darkmeow.irc.data.enmus.EnumUserPremium
import net.darkmeow.irc.network.IRCNetworkManagerServer
import net.darkmeow.irc.network.packet.online.c2s.C2SPacketMessage
import net.darkmeow.irc.network.packet.online.s2c.S2CPacketPrivateMessageResult
import net.darkmeow.irc.network.packet.online.s2c.S2CPacketSessionMessage
import net.darkmeow.irc.utils.userdata.UserdataIgnoreUtils.getUserIgnores
import java.util.*

class HandlePacketMessage(private val connection: IRCNetworkManagerServer): SimpleChannelInboundHandler<C2SPacketMessage>() {

    override fun channelRead0(ctx: ChannelHandlerContext, packet: C2SPacketMessage) {
        when (packet.type) {
            C2SPacketMessage.Type.PUBLIC -> {
                if (connection.userPremium.ordinal < EnumUserPremium.USER.ordinal) {
                    connection.sendSystemMessage("无权限发言")
                    return
                }

                val boardCastPacket = S2CPacketSessionMessage(
                    S2CPacketSessionMessage.Type.PUBLIC,
                    connection.sessionId,
                    DataUser(connection.user, connection.userPremium, connection.sessionState),
                    packet.message
                        .replace("&", "§")
                        .replace("\n", "")
                        .replace("\r", ""),
                    UUID.randomUUID()
                )

                connection.bossNetworkManager.logger.info("[${connection.user}] ${boardCastPacket.message}")

                connection.bossNetworkManager.clients.values
                    // 屏蔽发送者的用户不会受到
                    .filter { !connection.bossNetworkManager.base.dataManager.getUserIgnores(it.user).contains(connection.user) }
                    .onEach {
                        // 同时也会发送给发送者客户端上 这不是bug 而是刻意这么设计的
                        it.sendPacket(boardCastPacket)
                    }
            }
            C2SPacketMessage.Type.PRIVATE -> {
                if (connection.userPremium.ordinal < EnumUserPremium.USER.ordinal) {
                    connection.sendSystemMessage("无权限发言")
                    return
                }

                connection.bossNetworkManager.clients.values
                    .takeIf { packet.arg.isNotEmpty() }
                    ?.filter { other -> other.user == packet.arg[0] || other.sessionId.toString() == packet.arg[0] }
                    ?.also {
                        // 接收方不在线
                        if (it.isEmpty()) {
                            connection.sendPacket(S2CPacketPrivateMessageResult(packet.arg[0]))
                            return
                        }
                    }
                    ?.also {
                        val id = UUID.randomUUID()
                        val boardCastPacket = S2CPacketSessionMessage(
                            S2CPacketSessionMessage.Type.PRIVATE,
                            connection.sessionId,
                            DataUser(connection.user, connection.userPremium, connection.sessionState),
                            packet.message
                                .replace("&", "§")
                                .replace("\n", "")
                                .replace("\r", ""),
                            id
                        )

                        connection.sendPacket(S2CPacketPrivateMessageResult(packet.arg[0], boardCastPacket.message, id))
                        // 兼容多客户端登录 (适用于 同一设备多开客户端)
                        it.onEach { other ->
                            other.sendPacket(boardCastPacket)
                        }
                    }
            }
            C2SPacketMessage.Type.COMMAND -> {
                connection.bossNetworkManager.logger.info("[${connection.user}] 使用指令: ${packet.message} ${packet.arg.joinToString(" ")}")
                connection.bossNetworkManager.base.commandManager.handle(connection, packet.message, packet.arg.toMutableList())
            }
        }
    }
}