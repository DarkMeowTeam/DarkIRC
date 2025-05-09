package net.darkmeow.irc.command.impl

import net.darkmeow.irc.command.Command
import net.darkmeow.irc.command.CommandManager
import net.darkmeow.irc.network.IRCNetworkManagerServer
import net.darkmeow.irc.utils.MessageUtils.sendCommandUsage
import net.darkmeow.irc.utils.userdata.UserdataIgnoreUtils.getUserIgnores
import net.darkmeow.irc.utils.userdata.UserdataIgnoreUtils.setUserIgnores

class CommandIgnore: Command("Ignore") {

    override fun handle(manager: CommandManager, connection: IRCNetworkManagerServer, args: MutableList<String>) {
        when (if (args.isEmpty()) "" else args[0]) {
            "add" -> {
                if (args.size != 2) {
                    connection.sendCommandUsage("ignore", "add <用户名/服务名>")
                    return
                }
                val ignores = manager.base.dataManager.getUserIgnores(connection.user)

                if (ignores.contains(args[1])) {
                    connection.sendSystemMessage("${args[1]} 已经处于屏蔽列表中")
                } else {
                    ignores.add(args[1])
                    manager.base.dataManager.setUserIgnores(connection.user, ignores)
                    connection.sendSystemMessage("已屏蔽 ${args[1]} 的消息")
                }
            }
            "remove" -> {
                if (args.size != 2) {
                    connection.sendCommandUsage("ignore", "remove <用户名/服务名>")
                    return
                }
                val ignores = manager.base.dataManager.getUserIgnores(connection.user)

                if (ignores.contains(args[1])) {
                    ignores.remove(args[1])
                    manager.base.dataManager.setUserIgnores(connection.user, ignores)
                    connection.sendSystemMessage("已取消屏蔽 ${args[1]} 的消息")
                } else {
                    connection.sendSystemMessage("${args[1]} 不处于屏蔽列表中")
                }
            }
            "list" -> {
                manager.base.dataManager.getUserIgnores(connection.user).also { users ->
                    connection.sendSystemMessage("已屏蔽列表(${users.size}): ${users.joinToString(", ")}")
                }
            }
            else -> connection.sendCommandUsage("ignore", "<add/remove/list> <...>")
        }
    }
}