package net.darkmeow.irc.command.impl

import net.darkmeow.irc.command.Command
import net.darkmeow.irc.command.CommandManager
import net.darkmeow.irc.data.base.DataClient
import net.darkmeow.irc.data.enmus.EnumUserPremium
import net.darkmeow.irc.database.extensions.DataManagerClientExtensions.clientExist
import net.darkmeow.irc.database.extensions.DataManagerClientExtensions.createClient
import net.darkmeow.irc.database.extensions.DataManagerClientExtensions.deleteClient
import net.darkmeow.irc.database.extensions.DataManagerClientExtensions.getClientMetadata
import net.darkmeow.irc.database.extensions.DataManagerClientExtensions.getClients
import net.darkmeow.irc.database.extensions.DataManagerClientExtensions.updateClientMetadata
import net.darkmeow.irc.database.extensions.DataManagerUserExtensions.userExist
import net.darkmeow.irc.network.IRCNetworkManagerServer
import net.darkmeow.irc.utils.MessageUtils.sendCommandUsage
import net.darkmeow.irc.utils.MessageUtils.sendMessageError
import net.darkmeow.irc.utils.user.UserPremiumUtils.isClientAdmin
import java.util.Base64

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
                if (!manager.base.dataManager.isClientAdmin(args[1], connection.user)) {
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
            "users" -> handleUsers(manager, connection, args)
            "admins" -> handleAdmins(manager, connection, args)
            else -> connection.sendCommandUsage("clients", "<create/delete/version/list/users/admins> <...>")
        }

    }

    fun handleAdmins(manager: CommandManager, connection: IRCNetworkManagerServer, args: MutableList<String>) {
        when (if (args.size <= 1) "" else args[1]) {
            "add" -> {
                if (args.size != 4) {
                    connection.sendCommandUsage("clients admins", "add <客户端> <用户名>")
                    return
                }

                manager.base.dataManager.apply {
                    if (clientExist(args[2])) {
                        if (!isClientAdmin(args[1], connection.user)) {
                            connection.sendMessageError("当前登录用户无权限执行该命令")
                            return
                        }

                        val admins = getClientMetadata(args[2]).metadata.clientAdministrators

                        if (admins.contains(args[3])) {
                            connection.sendSystemMessage("客户端 ${args[2]} 管理员用户 ${args[3]} 添加失败:用户已在列表")
                        } else if (userExist(args[3])) {
                            connection.sendSystemMessage("客户端 ${args[2]} 管理员用户 ${args[3]} 添加失败:用户不存在")
                        } else {
                            admins.add(args[3])
                            manager.base.dataManager.updateClientMetadata(name = args[2], clientAdministrators = admins)
                            connection.sendSystemMessage("客户端 ${args[2]} 管理员用户 ${args[3]} 添加成功")
                        }
                    } else {
                        connection.sendSystemMessage("客户端 ${args[2]} 不存在")
                    }
                }
            }
            "remove" -> {
                if (args.size != 4) {
                    connection.sendCommandUsage("clients admins", "remove <客户端> <用户名>")
                    return
                }

                manager.base.dataManager.apply {
                    if (clientExist(args[2])) {
                        if (!isClientAdmin(args[1], connection.user)) {
                            connection.sendMessageError("当前登录用户无权限执行该命令")
                            return
                        }

                        val admins = getClientMetadata(args[2]).metadata.clientAdministrators

                        if (admins.contains(args[3])) {
                            admins.remove(args[3])
                            manager.base.dataManager.updateClientMetadata(name = args[2], clientAdministrators = admins)
                            connection.sendSystemMessage("客户端 ${args[2]} 管理员用户 ${args[3]} 删除成功")
                        } else {
                            connection.sendSystemMessage("客户端 ${args[2]} 管理员用户 ${args[3]} 添删除失败:用户不在列表")
                        }
                    } else {
                        connection.sendSystemMessage("客户端 ${args[2]} 不存在")
                    }
                }
            }
            "list" -> {
                manager.base.dataManager.apply {
                    if (clientExist(args[2])) {
                        if (!isClientAdmin(args[1], connection.user)) {
                            connection.sendMessageError("当前登录用户无权限执行该命令")
                            return
                        }

                        val users = getClientMetadata(args[2]).metadata.clientAdministrators

                        connection.sendSystemMessage("客户端(${args[2]})管理员用户列表(${users.size}): ${users.joinToString(", ")}")
                    } else {
                        connection.sendSystemMessage("客户端 ${args[2]} 不存在")
                    }
                }
            }
            else -> connection.sendCommandUsage("clients admins", "<add/remove/list> <...>")
        }
    }

    fun handleUsers(manager: CommandManager, connection: IRCNetworkManagerServer, args: MutableList<String>) {
        when (if (args.size <= 1) "" else args[1]) {
            "add" -> {
                if (args.size != 4) {
                    connection.sendCommandUsage("clients users", "add <客户端> <用户名>")
                    return
                }

                manager.base.dataManager.apply {
                    if (clientExist(args[2])) {
                        if (!isClientAdmin(args[1], connection.user)) {
                            connection.sendMessageError("当前登录用户无权限执行该命令")
                            return
                        }

                        val users = getClientMetadata(args[2]).metadata.clientUsers

                        if (users.contains(args[3])) {
                            connection.sendSystemMessage("客户端 ${args[2]} 授权用户 ${args[3]} 添加失败:用户已在列表")
                        } else if (userExist(args[3])) {
                            connection.sendSystemMessage("客户端 ${args[2]} 授权用户 ${args[3]} 添加失败:用户不存在")
                        } else {
                            users.add(args[3])
                            manager.base.dataManager.updateClientMetadata(name = args[2], clientUsers = users)
                            connection.sendSystemMessage("客户端 ${args[2]} 授权用户 ${args[3]} 添加成功")
                        }
                    } else {
                        connection.sendSystemMessage("客户端 ${args[2]} 不存在")
                    }
                }
            }
            "remove" -> {
                if (args.size != 4) {
                    connection.sendCommandUsage("clients users", "remove <客户端> <用户名>")
                    return
                }

                manager.base.dataManager.apply {
                    if (clientExist(args[2])) {
                        if (!isClientAdmin(args[1], connection.user)) {
                            connection.sendMessageError("当前登录用户无权限执行该命令")
                            return
                        }

                        val users = getClientMetadata(args[2]).metadata.clientUsers

                        if (users.contains(args[3])) {
                            users.remove(args[3])
                            manager.base.dataManager.updateClientMetadata(name = args[2], clientUsers = users)
                            connection.sendSystemMessage("客户端 ${args[2]} 授权用户 ${args[3]} 删除成功")
                        } else {
                            connection.sendSystemMessage("客户端 ${args[2]} 授权用户 ${args[3]} 添删除失败:用户不在列表")
                        }
                    } else {
                        connection.sendSystemMessage("客户端 ${args[2]} 不存在")
                    }
                }
            }
            "list" -> {
                manager.base.dataManager.apply {
                    if (clientExist(args[2])) {
                        if (!isClientAdmin(args[1], connection.user)) {
                            connection.sendMessageError("当前登录用户无权限执行该命令")
                            return
                        }

                        val users = getClientMetadata(args[2]).metadata.clientUsers

                        connection.sendSystemMessage("客户端(${args[2]})授权用户列表(${users.size}): ${users.joinToString(", ")}")
                    } else {
                        connection.sendSystemMessage("客户端 ${args[2]} 不存在")
                    }
                }
            }

            else -> connection.sendCommandUsage("clients users", "<add/remove/list> <...>")
        }
    }

}