package net.darkmeow.irc.command.impl

import io.netty.channel.Channel
import net.darkmeow.irc.command.Command
import net.darkmeow.irc.command.CommandManager
import net.darkmeow.irc.utils.ChannelUtils.sendSystemMessage

class CommandPing: Command("Ping") {

    override fun handle(manager: CommandManager, channel: Channel, args: MutableList<String>) {
        channel.sendSystemMessage("Pong!")
    }

}