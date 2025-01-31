package net.darkmeow.irc.utils

import io.netty.channel.Channel
import net.darkmeow.irc.IRCServer
import net.darkmeow.irc.network.PacketUtils
import net.darkmeow.irc.network.packet.s2c.S2CPacket
import net.darkmeow.irc.network.packet.s2c.S2CPacketMessageSystem
import net.darkmeow.irc.utils.ChannelAttrUtils.getCurrentUser

object ChannelUtils {

    fun Channel.sendServiceMessage(base: IRCServer, service: String, message: String) {
        getCurrentUser()
            ?.takeIf { base.dataManager.getUserdataIgnores(it).contains(service) }
            ?.also { return }

        sendPacket(S2CPacketMessageSystem(message))
    }

    fun Channel.sendSystemMessage(message: String) {
        sendPacket(S2CPacketMessageSystem(message))
    }

    fun Channel.sendPacket(packet: S2CPacket) {
        writeAndFlush(PacketUtils.generatePacket(packet))
    }

}