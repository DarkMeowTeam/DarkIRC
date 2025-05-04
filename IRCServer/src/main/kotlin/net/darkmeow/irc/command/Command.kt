package net.darkmeow.irc.command

import net.darkmeow.irc.network.IRCNetworkManagerServer

abstract class Command(vararg val root: String) {
    abstract fun handle(manager: CommandManager, connection: IRCNetworkManagerServer, args: MutableList<String>)
}