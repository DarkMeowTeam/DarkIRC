package net.darkmeow.irc.utils

import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import net.darkmeow.irc.network.PacketUtils
import net.darkmeow.irc.network.packet.s2c.S2CPacket
import net.darkmeow.irc.network.packet.s2c.S2CPacketMessageSystem

object ChannelUtils {
    fun Channel.sendSystemMessage(message: String) {
        sendPacket(S2CPacketMessageSystem(message))
    }

    fun Channel.sendPacket(packet: S2CPacket) {
        writeAndFlush(PacketUtils.generatePacket(packet))
    }

}