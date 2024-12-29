package net.darkmeow.irc.command

import io.netty.channel.ChannelHandlerContext

abstract class Command(val root: String) {
    abstract fun handle(ctx: ChannelHandlerContext, args: MutableList<String>)
}