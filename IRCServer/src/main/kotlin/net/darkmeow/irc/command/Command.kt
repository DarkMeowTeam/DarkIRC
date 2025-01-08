package net.darkmeow.irc.command

import io.netty.channel.Channel

abstract class Command(vararg val root: String) {
    abstract fun handle(manager: CommandManager, channel: Channel, args: MutableList<String>)
}