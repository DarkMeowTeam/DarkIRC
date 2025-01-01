package net.darkmeow.irc.command

import io.netty.channel.ChannelHandlerContext

abstract class Command(vararg val root: String) {
    abstract fun handle(manager: CommandManager, ctx: ChannelHandlerContext, args: MutableList<String>)
}