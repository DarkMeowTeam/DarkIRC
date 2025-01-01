package net.darkmeow.irc.command

import io.netty.channel.ChannelHandlerContext
import net.darkmeow.irc.command.impl.CommandPing

class CommandManager {

    @JvmField
    val commands = arrayOf(
        CommandPing()
    )

    fun handle(ctx: ChannelHandlerContext, root: String, args: MutableList<String>) = commands
        .firstOrNull { it.root.equals(other = root, ignoreCase = true) }
        ?.handle(ctx, args)

}