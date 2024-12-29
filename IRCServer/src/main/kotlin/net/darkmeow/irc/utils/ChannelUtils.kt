package net.darkmeow.irc.utils

import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import net.darkmeow.irc.network.PacketUtils
import net.darkmeow.irc.network.packet.s2c.S2CPacket
import net.darkmeow.irc.network.packet.s2c.S2CPacketMessageSystem

object ChannelUtils {
    fun ChannelHandlerContext.sendPacket(packet: S2CPacket) {
        channel().sendPacket(packet)
    }

    fun ChannelHandlerContext.sendSystemMessage(message: String) {
        channel().sendPacket(S2CPacketMessageSystem(message))
    }

    fun Channel.sendPacket(packet: S2CPacket) {
        writeAndFlush(PacketUtils.generatePacket(packet))
    }
}