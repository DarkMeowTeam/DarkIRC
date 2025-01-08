package net.darkmeow.irc.utils

import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import net.darkmeow.irc.utils.ChannelUtils.sendSystemMessage

object MessageUtils {

    fun Channel.sendCommandUsage(root: String,syntax: String) = this.sendSystemMessage("§7指令用法: /irc:$root $syntax")

    fun Channel.sendMessageError(message: String) = this.sendSystemMessage("§c$message")

}