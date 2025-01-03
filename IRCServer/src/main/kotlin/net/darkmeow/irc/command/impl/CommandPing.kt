package net.darkmeow.irc.command.impl

import io.netty.channel.ChannelHandlerContext
import net.darkmeow.irc.command.Command
import net.darkmeow.irc.command.CommandManager
import net.darkmeow.irc.utils.ChannelUtils.sendSystemMessage

class CommandPing: Command("Ping") {

    override fun handle(manager: CommandManager, ctx: ChannelHandlerContext, args: MutableList<String>) {
        ctx.sendSystemMessage("Pong!")
    }

}