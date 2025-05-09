package net.darkmeow.irc.utils

import net.darkmeow.irc.IRCServer
import net.darkmeow.irc.network.IRCNetworkManagerServer
import net.darkmeow.irc.utils.userdata.UserdataIgnoreUtils.getUserIgnores

object MessageUtils {

    fun IRCNetworkManagerServer.sendServiceMessage(base: IRCServer, service: String, message: String) {
        if (base.dataManager.getUserIgnores(user).contains(service)) return

        sendSystemMessage(message)
    }

    fun IRCNetworkManagerServer.sendCommandUsage(root: String,syntax: String) = this.sendSystemMessage("§7指令用法: /irc:$root $syntax")

    fun IRCNetworkManagerServer.sendMessageError(message: String) = this.sendSystemMessage("§c$message")

}