package net.darkmeow.irc.utils

import net.darkmeow.irc.network.IRCNetworkManagerServer
import net.darkmeow.irc.network.NetworkManager
import net.darkmeow.irc.network.packet.S2CPacket

object QuickBoardCastUtils {

    fun NetworkManager.sendPacketToAll(packet: S2CPacket, takeIf: (IRCNetworkManagerServer) -> Boolean = { true }) {
        clients.values
            .filter { takeIf(it) }
            .forEach { it.sendPacket(packet) }
    }

    fun NetworkManager.sendPacketToAllIgnoreInvisible(connection: IRCNetworkManagerServer, packet: S2CPacket) = sendPacketToAll(packet) { client ->
        !connection.currentIsInvisible || client.user == connection.user
    }

    /**
     * 向所有在线会话发送数据
     * 如果当前客户端在隐身状态则只向同用户名会话发送
     */
    fun IRCNetworkManagerServer.sendPacketToAllIgnoreInvisible(packet: S2CPacket) = bossNetworkManager.sendPacketToAllIgnoreInvisible(this, packet)
}