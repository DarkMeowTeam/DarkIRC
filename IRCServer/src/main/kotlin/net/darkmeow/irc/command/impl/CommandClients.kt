package net.darkmeow.irc.command.impl

import net.darkmeow.irc.command.Command
import net.darkmeow.irc.command.CommandManager
import net.darkmeow.irc.data.base.DataClient
import net.darkmeow.irc.data.enmus.EnumUserPremium
import net.darkmeow.irc.database.extensions.DataManagerClientExtensions.clientExist
import net.darkmeow.irc.database.extensions.DataManagerClientExtensions.createClient
import net.darkmeow.irc.database.extensions.DataManagerClientExtensions.deleteClient
import net.darkmeow.irc.database.extensions.DataManagerClientExtensions.getClients
import net.darkmeow.irc.database.extensions.DataManagerClientExtensions.updateClientMetadata
import net.darkmeow.irc.network.IRCNetworkManagerServer
import net.darkmeow.irc.utils.MessageUtils.sendCommandUsage
import net.darkmeow.irc.utils.MessageUtils.sendMessageError
import java.util.*

class CommandClients: Command("Clients") {

    override fun handle(manager: CommandManager, connection: IRCNetworkManagerServer, args: MutableList<String>) {
        when (if (args.isEmpty()) "" else args[0]) {
            "create" -> {
                if (args.size != 4) {
                    connection.sendCommandUsage("clients", "create <客户端名> <最低允许登录版本>")
                    return
                }
                if (connection.userPremium != EnumUserPremium.OWNER) {
                    connection.sendMessageError("当前登录用户无权限执行该命令")
                    return
                }
                manager.base.dataManager.apply {
                    createClient(args[1], DataClient.ClientMetadata(args[3].toIntOrNull() ?: 0)).also { client ->
                        connection.sendSystemMessage("成功创建客户端 ${client.name}, 连接密钥: ${Base64.getEncoder().encodeToString(client.key.private.encoded)} (只显示一次, 请妥善保存)")
                    }
                }
            }
            "delete" -> {
                if (args.size != 2) {
                    connection.sendCommandUsage("clients", "delete <客户端>")
                    return
                }
                if (connection.userPremium != EnumUserPremium.OWNER) {
                    connection.sendMessageError("当前登录用户无权限执行该命令")
                    return
                }
                manager.base.dataManager.apply {
                    if (clientExist(args[1])) {
                        deleteClient(args[1])
                        connection.sendSystemMessage("成功删除客户端 ${args[1]} (已登录用户暂不受影响)")
                    } else {
                        connection.sendSystemMessage("客户端 ${args[1]} 不存在")
                    }
                }

            }
            "version" -> {
                if (args.size != 3) {
                    connection.sendCommandUsage("clients", "version <客户端Id> <最低允许登录版本>")
                    return
                }
                if (connection.userPremium != EnumUserPremium.OWNER) {
                    connection.sendMessageError("当前登录用户无权限执行该命令")
                    return
                }

                manager.base.dataManager.apply {
                    if (clientExist(args[1])) {
                        manager.base.dataManager.updateClientMetadata(name = args[1], allowLoginMinVersion = args[2].toIntOrNull() ?: 0)
                        connection.sendSystemMessage("成功修改客户端 ${args[1]} 最低允许登录版本为 ${args[2].toIntOrNull() ?: 0} (已登录用户暂不受影响)")
                    } else {
                        connection.sendSystemMessage("客户端 ${args[1]} 不存在")
                    }
                }

            }
            "list" -> {
                if (connection.userPremium != EnumUserPremium.OWNER) {
                    connection.sendMessageError("当前登录用户无权限执行该命令")
                    return
                }
                manager.base.dataManager.getClients().also { users ->
                    connection.sendSystemMessage("客户端列表(${users.size}): ${users.joinToString(", ")}")
                }
            }
            else -> connection.sendCommandUsage("clients", "<create/delete/version/list> <...>")
        }

    }

}