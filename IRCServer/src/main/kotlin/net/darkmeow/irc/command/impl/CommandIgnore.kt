package net.darkmeow.irc.command.impl

import net.darkmeow.irc.command.Command
import net.darkmeow.irc.command.CommandManager
import net.darkmeow.irc.network.IRCNetworkManagerServer
import net.darkmeow.irc.utils.MessageUtils.sendCommandUsage

class CommandIgnore: Command("Ignore") {

    override fun handle(manager: CommandManager, connection: IRCNetworkManagerServer, args: MutableList<String>) {
        when (if (args.isEmpty()) "" else args[0]) {
            "add" -> {
                if (args.size != 2) {
                    connection.sendCommandUsage("ignore", "add <用户名/服务名>")
                    return
                }
                manager.base.dataManager.getUserdataIgnores(connection.user)
                    .also {
                        if (it.contains(args[1])) {
                            connection.sendSystemMessage("${args[1]} 已经处于屏蔽列表中")
                        } else {
                            it.add(args[1])
                            manager.base.dataManager.setUserdataIgnores(connection.user, it)
                            connection.sendSystemMessage("已屏蔽 ${args[1]} 的消息")
                        }
                    }
            }
            "remove" -> {
                if (args.size != 2) {
                    connection.sendCommandUsage("ignore", "remove <用户名/服务名>")
                    return
                }
                manager.base.dataManager.getUserdataIgnores(connection.user)
                    .also {
                        if (!it.contains(args[1])) {
                            connection.sendSystemMessage("${args[1]} 不处于屏蔽列表中")
                        } else {
                            it.remove(args[1])
                            manager.base.dataManager.setUserdataIgnores(connection.user, it)
                            connection.sendSystemMessage("已取消屏蔽 ${args[1]} 的消息")
                        }
                    }
            }
            "list" -> {
                manager.base.dataManager.getUserdataIgnores(connection.user).also { users ->
                    connection.sendSystemMessage("已屏蔽列表(${users.size}): ${users.joinToString(", ")}")
                }
            }
            else -> connection.sendCommandUsage("ignore", "<add/remove/list> <...>")
        }
    }
}