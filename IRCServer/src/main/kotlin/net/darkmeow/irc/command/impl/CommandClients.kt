package net.darkmeow.irc.command.impl

import net.darkmeow.irc.command.Command
import net.darkmeow.irc.command.CommandManager
import net.darkmeow.irc.data.enmus.EnumUserPremium
import net.darkmeow.irc.network.IRCNetworkManagerServer
import net.darkmeow.irc.utils.MessageUtils.sendCommandUsage
import net.darkmeow.irc.utils.MessageUtils.sendMessageError

class CommandClients: Command("Clients") {

    override fun handle(manager: CommandManager, connection: IRCNetworkManagerServer, args: MutableList<String>) {
        when (if (args.isEmpty()) "" else args[0]) {
            "create" -> {
                if (args.size != 4) {
                    connection.sendCommandUsage("clients", "create <客户端Id> <客户端Hash> <最低允许登录版本>")
                    return
                }
                if (connection.userPremium != EnumUserPremium.OWNER) {
                    connection.sendMessageError("当前登录用户无权限执行该命令")
                    return
                }
                manager.base.dataManager.createClient(args[1], args[2], args[3].toIntOrNull() ?: 0)
                connection.sendSystemMessage("成功创建客户端 ${args[1]}")
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
                if (manager.base.dataManager.clientExist(args[1])) {
                    manager.base.dataManager.deleteClient(args[1])
                    connection.sendSystemMessage("成功删除客户端 ${args[1]} (已登录用户暂不受影响)")
                } else {
                    connection.sendSystemMessage("客户端 ${args[1]} 不存在")
                }
            }
            "version" -> {
                if (args.size != 3) {
                    connection.sendCommandUsage("clients", "version <客户端Id> <最低允许登录版本>")
                    return
                }
                // 超级管理员 / 客户端管理员
                if (connection.userPremium != EnumUserPremium.OWNER || manager.base.dataManager.getClientAdministrators(args[1])?.contains(connection.user) == true) {
                    connection.sendMessageError("当前登录用户无权限执行该命令")
                    return
                }
                if (manager.base.dataManager.clientExist(args[1])) {
                    manager.base.dataManager.setClientMinVersion(args[1], args[2].toIntOrNull() ?: 0)
                    connection.sendSystemMessage("成功修改客户端 ${args[1]} 最低允许登录版本为 ${args[2].toIntOrNull() ?: 0} (已登录用户暂不受影响)")
                } else {
                    connection.sendSystemMessage("客户端 ${args[1]} 不存在")
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
            "users" -> {
                // 超级管理员 / 客户端管理员
                val hasPremium = connection.userPremium == EnumUserPremium.OWNER
                    || manager.base.dataManager.getClientAdministrators(if (args.size < 3) "null" else args[2])?.contains(connection.user) == true

                when (if (args.size <= 1) "" else args[1]) {
                    "add" -> {
                        if (args.size != 4) {
                            connection.sendCommandUsage("clients users", "add <客户端> <用户名>")
                            return
                        }
                        if (!hasPremium) {
                            connection.sendMessageError("当前登录用户无权限执行该命令")
                            return
                        }
                        if (manager.base.dataManager.clientExist(args[2])) {
                            manager.base.dataManager.getClientUsers(args[2])
                                ?.also {
                                    if (it.contains(args[3])) {
                                        connection.sendSystemMessage("客户端 ${args[2]} 授权用户 ${args[3]} 已经存在")
                                    } else if (!manager.base.dataManager.userExist(args[3])) {
                                        connection.sendSystemMessage("客户端 ${args[2]} 授权用户 ${args[3]} 添加失败:用户不存在")
                                    } else {
                                        it.add(args[3])
                                        manager.base.dataManager.setClientUsers(args[2], it)
                                        connection.sendSystemMessage("客户端 ${args[2]} 授权用户 ${args[3]} 添加成功")
                                    }
                                }
                                ?: run {
                                    connection.sendSystemMessage("客户端 ${args[2]} 授权用户 ${args[3]} 添加失败:请联系超级管理员")
                                }
                        } else {
                            connection.sendSystemMessage("客户端 ${args[2]} 不存在")
                        }
                    }
                    "remove" -> {
                        if (args.size != 4) {
                            connection.sendCommandUsage("clients users", "remove <客户端> <用户名>")
                            return
                        }
                        if (!hasPremium) {
                            connection.sendMessageError("当前登录用户无权限执行该命令")
                            return
                        }
                        if (manager.base.dataManager.clientExist(args[2])) {
                            manager.base.dataManager.getClientUsers(args[2])
                                ?.also {
                                    if (!it.contains(args[3])) {
                                        connection.sendSystemMessage("客户端 ${args[2]} 授权用户 ${args[3]} 不存在")
                                    } else {
                                        it.remove(args[3])
                                        manager.base.dataManager.setClientUsers(args[2], it)
                                        connection.sendSystemMessage("客户端 ${args[2]} 授权用户 ${args[3]} 删除成功")
                                    }
                                }
                                ?: run {
                                    connection.sendSystemMessage("客户端 ${args[2]} 授权用户 ${args[3]} 删除失败:请联系超级管理员")
                                }
                        } else {
                            connection.sendSystemMessage("客户端 ${args[2]} 不存在")
                        }
                    }
                    "list" -> {
                        if (!hasPremium) {
                            connection.sendMessageError("当前登录用户无权限执行该命令")
                            return
                        }
                        manager.base.dataManager.getClientUsers(args[2])
                            ?.also { users ->
                                connection.sendSystemMessage("客户端(${args[2]})授权用户列表(${users.size}): ${users.joinToString(", ")}")
                            }
                    }

                    else -> connection.sendCommandUsage("clients users", "<add/remove/list> <...>")
                }
            }
            "admins" -> {
                // 超级管理员 / 客户端管理员
                val hasPremium = connection.userPremium == EnumUserPremium.OWNER
                    || manager.base.dataManager.getClientAdministrators(if (args.size < 3) "null" else args[2])?.contains(connection.user) == true

                when (if (args.size <= 1) "" else args[1]) {
                    "add" -> {
                        if (args.size != 4) {
                            connection.sendCommandUsage("clients admins", "add <客户端> <用户名>")
                            return
                        }
                        if (!hasPremium) {
                            connection.sendMessageError("当前登录用户无权限执行该命令")
                            return
                        }
                        if (manager.base.dataManager.clientExist(args[2])) {
                            manager.base.dataManager.getClientAdministrators(args[2])
                                ?.also {
                                    if (it.contains(args[3])) {
                                        connection.sendSystemMessage("客户端 ${args[2]} 管理员用户 ${args[3]} 已经存在")
                                    } else if (!manager.base.dataManager.userExist(args[3])) {
                                        connection.sendSystemMessage("客户端 ${args[2]} 管理员用户 ${args[3]} 添加失败:用户不存在")
                                    } else {
                                        it.add(args[3])
                                        manager.base.dataManager.setClientAdministrators(args[2], it)
                                        connection.sendSystemMessage("客户端 ${args[2]} 管理员用户 ${args[3]} 添加成功")
                                    }
                                }
                                ?: run {
                                    connection.sendSystemMessage("客户端 ${args[2]} 管理员用户 ${args[3]} 添加失败:请联系超级管理员")
                                }
                        } else {
                            connection.sendSystemMessage("客户端 ${args[2]} 不存在")
                        }
                    }
                    "remove" -> {
                        if (args.size != 4) {
                            connection.sendCommandUsage("clients admins", "remove <客户端> <用户名>")
                            return
                        }
                        if (!hasPremium) {
                            connection.sendMessageError("当前登录用户无权限执行该命令")
                            return
                        }
                        if (manager.base.dataManager.clientExist(args[2])) {
                            manager.base.dataManager.getClientAdministrators(args[2])
                                ?.also {
                                    if (!it.contains(args[3])) {
                                        connection.sendSystemMessage("客户端 ${args[2]} 管理员用户 ${args[3]} 不存在")
                                    } else {
                                        it.remove(args[3])
                                        manager.base.dataManager.setClientAdministrators(args[2], it)
                                        connection.sendSystemMessage("客户端 ${args[2]} 管理员用户 ${args[3]} 删除成功")
                                    }
                                }
                                ?: run {
                                    connection.sendSystemMessage("客户端 ${args[2]} 授权用户 ${args[3]} 删除失败:请联系超级管理员")
                                }
                        } else {
                            connection.sendSystemMessage("客户端 ${args[2]} 不存在")
                        }
                    }
                    "list" -> {
                        if (!hasPremium) {
                            connection.sendMessageError("当前登录用户无权限执行该命令")
                            return
                        }
                        manager.base.dataManager.getClientAdministrators(args[2])
                            ?.also { users ->
                                connection.sendSystemMessage("客户端(${args[2]})管理员用户列表(${users.size}): ${users.joinToString(", ")}")
                            }
                    }

                    else -> connection.sendCommandUsage("clients admins", "<add/remove/list> <...>")
                }
            }
            else -> connection.sendCommandUsage("clients", "<create/delete/version/list/users/admins> <...>")
        }

    }

}