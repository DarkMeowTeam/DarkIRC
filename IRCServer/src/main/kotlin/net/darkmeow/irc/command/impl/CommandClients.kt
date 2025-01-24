package net.darkmeow.irc.command.impl

import io.netty.channel.Channel
import net.darkmeow.irc.command.Command
import net.darkmeow.irc.command.CommandManager
import net.darkmeow.irc.network.packet.s2c.S2CPacketUpdateMySessionInfo
import net.darkmeow.irc.utils.ChannelAttrUtils.getCurrentUser
import net.darkmeow.irc.utils.ChannelUtils.sendSystemMessage
import net.darkmeow.irc.utils.DataManagerUtils.getCTXPremium
import net.darkmeow.irc.utils.MessageUtils.sendCommandUsage
import net.darkmeow.irc.utils.MessageUtils.sendMessageError

class CommandClients: Command("Clients") {

    override fun handle(manager: CommandManager, channel: Channel, args: MutableList<String>) {
        when (if (args.isEmpty()) "" else args[0]) {
            "create" -> {
                if (args.size != 4) {
                    channel.sendCommandUsage("clients", "create <客户端Id> <客户端Hash> <最低允许登录版本>")
                    return
                }
                if (manager.base.dataManager.getCTXPremium(channel) != S2CPacketUpdateMySessionInfo.Premium.SUPER_ADMIN) {
                    channel.sendMessageError("当前登录用户无权限执行该命令")
                    return
                }
                manager.base.dataManager.createClient(args[1], args[2], args[3].toIntOrNull() ?: 0)
                channel.sendSystemMessage("成功创建客户端 ${args[1]}")
            }
            "delete" -> {
                if (args.size != 2) {
                    channel.sendCommandUsage("clients", "delete <客户端>")
                    return
                }
                if (manager.base.dataManager.getCTXPremium(channel) != S2CPacketUpdateMySessionInfo.Premium.SUPER_ADMIN) {
                    channel.sendMessageError("当前登录用户无权限执行该命令")
                    return
                }
                if (manager.base.dataManager.clientExist(args[1])) {
                    manager.base.dataManager.deleteClient(args[1])
                    channel.sendSystemMessage("成功删除客户端 ${args[1]} (已登录用户暂不受影响)")
                } else {
                    channel.sendSystemMessage("客户端 ${args[1]} 不存在")
                }
            }
            "version" -> {
                if (args.size != 2) {
                    channel.sendCommandUsage("clients", "version <客户端Id> <最低允许登录版本>")
                    return
                }
                // 超级管理员 / 客户端管理员
                if (manager.base.dataManager.getCTXPremium(channel) != S2CPacketUpdateMySessionInfo.Premium.SUPER_ADMIN || manager.base.dataManager.getClientAdministrators(args[1])?.contains(channel.getCurrentUser()) == true) {
                    channel.sendMessageError("当前登录用户无权限执行该命令")
                    return
                }
                if (manager.base.dataManager.clientExist(args[1])) {
                    manager.base.dataManager.setClientMinVersion(args[1], args[2].toIntOrNull() ?: 0)
                    channel.sendSystemMessage("成功修改客户端 ${args[1]} 最低允许登录版本为 ${args[2].toIntOrNull() ?: 0} (已登录用户暂不受影响)")
                } else {
                    channel.sendSystemMessage("客户端 ${args[1]} 不存在")
                }
            }
            "list" -> {
                if (manager.base.dataManager.getCTXPremium(channel) != S2CPacketUpdateMySessionInfo.Premium.SUPER_ADMIN) {
                    channel.sendMessageError("当前登录用户无权限执行该命令")
                    return
                }
                manager.base.dataManager.getClients().also { users ->
                    channel.sendSystemMessage("客户端列表(${users.size}): ${users.joinToString(", ")}")
                }
            }
            "users" -> {
                // 超级管理员 / 客户端管理员
                val hasPremium = manager.base.dataManager.getCTXPremium(channel) == S2CPacketUpdateMySessionInfo.Premium.SUPER_ADMIN
                    || manager.base.dataManager.getClientAdministrators(if (args.size < 3) "null" else args[2])?.contains(channel.getCurrentUser()) == true

                when (if (args.size <= 1) "" else args[1]) {
                    "add" -> {
                        if (args.size != 4) {
                            channel.sendCommandUsage("clients users", "add <客户端> <用户名>")
                            return
                        }
                        if (!hasPremium) {
                            channel.sendMessageError("当前登录用户无权限执行该命令")
                            return
                        }
                        if (manager.base.dataManager.clientExist(args[2])) {
                            manager.base.dataManager.getClientUsers(args[2])
                                ?.also {
                                    if (it.contains(args[3])) {
                                        channel.sendSystemMessage("客户端 ${args[2]} 授权用户 ${args[3]} 已经存在")
                                    } else if (!manager.base.dataManager.userExist(args[3])) {
                                        channel.sendSystemMessage("客户端 ${args[2]} 授权用户 ${args[3]} 添加失败:用户不存在")
                                    } else {
                                        it.add(args[3])
                                        manager.base.dataManager.setClientUsers(args[2], it)
                                        channel.sendSystemMessage("客户端 ${args[2]} 授权用户 ${args[3]} 添加成功")
                                    }
                                }
                                ?: run {
                                    channel.sendSystemMessage("客户端 ${args[2]} 授权用户 ${args[3]} 添加失败:请联系超级管理员")
                                }
                        } else {
                            channel.sendSystemMessage("客户端 ${args[2]} 不存在")
                        }
                    }
                    "remove" -> {
                        if (args.size != 4) {
                            channel.sendCommandUsage("clients users", "remove <客户端> <用户名>")
                            return
                        }
                        if (!hasPremium) {
                            channel.sendMessageError("当前登录用户无权限执行该命令")
                            return
                        }
                        if (manager.base.dataManager.clientExist(args[2])) {
                            manager.base.dataManager.getClientUsers(args[2])
                                ?.also {
                                    if (!it.contains(args[3])) {
                                        channel.sendSystemMessage("客户端 ${args[2]} 授权用户 ${args[3]} 不存在")
                                    } else {
                                        it.remove(args[3])
                                        manager.base.dataManager.setClientUsers(args[2], it)
                                        channel.sendSystemMessage("客户端 ${args[2]} 授权用户 ${args[3]} 删除成功")
                                    }
                                }
                                ?: run {
                                    channel.sendSystemMessage("客户端 ${args[2]} 授权用户 ${args[3]} 删除失败:请联系超级管理员")
                                }
                        } else {
                            channel.sendSystemMessage("客户端 ${args[2]} 不存在")
                        }
                    }
                    "list" -> {
                        if (!hasPremium) {
                            channel.sendMessageError("当前登录用户无权限执行该命令")
                            return
                        }
                        manager.base.dataManager.getClientUsers(args[2])
                            ?.also { users ->
                                channel.sendSystemMessage("客户端(${args[2]})授权用户列表(${users.size}): ${users.joinToString(", ")}")
                            }
                    }

                    else -> channel.sendCommandUsage("clients users", "<add/remove/list> <...>")
                }
            }
            "admins" -> {
                // 超级管理员 / 客户端管理员
                val hasPremium = manager.base.dataManager.getCTXPremium(channel) == S2CPacketUpdateMySessionInfo.Premium.SUPER_ADMIN
                    || manager.base.dataManager.getClientAdministrators(if (args.size < 3) "null" else args[2])?.contains(channel.getCurrentUser()) == true

                when (if (args.size <= 1) "" else args[1]) {
                    "add" -> {
                        if (args.size != 4) {
                            channel.sendCommandUsage("clients admins", "add <客户端> <用户名>")
                            return
                        }
                        if (!hasPremium) {
                            channel.sendMessageError("当前登录用户无权限执行该命令")
                            return
                        }
                        if (manager.base.dataManager.clientExist(args[2])) {
                            manager.base.dataManager.getClientAdministrators(args[2])
                                ?.also {
                                    if (it.contains(args[3])) {
                                        channel.sendSystemMessage("客户端 ${args[2]} 管理员用户 ${args[3]} 已经存在")
                                    } else if (!manager.base.dataManager.userExist(args[3])) {
                                        channel.sendSystemMessage("客户端 ${args[2]} 管理员用户 ${args[3]} 添加失败:用户不存在")
                                    } else {
                                        it.add(args[3])
                                        manager.base.dataManager.setClientAdministrators(args[2], it)
                                        channel.sendSystemMessage("客户端 ${args[2]} 管理员用户 ${args[3]} 添加成功")
                                    }
                                }
                                ?: run {
                                    channel.sendSystemMessage("客户端 ${args[2]} 管理员用户 ${args[3]} 添加失败:请联系超级管理员")
                                }
                        } else {
                            channel.sendSystemMessage("客户端 ${args[2]} 不存在")
                        }
                    }
                    "remove" -> {
                        if (args.size != 4) {
                            channel.sendCommandUsage("clients admins", "remove <客户端> <用户名>")
                            return
                        }
                        if (!hasPremium) {
                            channel.sendMessageError("当前登录用户无权限执行该命令")
                            return
                        }
                        if (manager.base.dataManager.clientExist(args[2])) {
                            manager.base.dataManager.getClientAdministrators(args[2])
                                ?.also {
                                    if (!it.contains(args[3])) {
                                        channel.sendSystemMessage("客户端 ${args[2]} 管理员用户 ${args[3]} 不存在")
                                    } else {
                                        it.remove(args[3])
                                        manager.base.dataManager.setClientAdministrators(args[2], it)
                                        channel.sendSystemMessage("客户端 ${args[2]} 管理员用户 ${args[3]} 删除成功")
                                    }
                                }
                                ?: run {
                                    channel.sendSystemMessage("客户端 ${args[2]} 授权用户 ${args[3]} 删除失败:请联系超级管理员")
                                }
                        } else {
                            channel.sendSystemMessage("客户端 ${args[2]} 不存在")
                        }
                    }
                    "list" -> {
                        if (!hasPremium) {
                            channel.sendMessageError("当前登录用户无权限执行该命令")
                            return
                        }
                        manager.base.dataManager.getClientAdministrators(args[2])
                            ?.also { users ->
                                channel.sendSystemMessage("客户端(${args[2]})管理员用户列表(${users.size}): ${users.joinToString(", ")}")
                            }
                    }

                    else -> channel.sendCommandUsage("clients admins", "<add/remove/list> <...>")
                }
            }
            else -> channel.sendCommandUsage("clients", "<create/delete/version/list/users/admins> <...>")
        }

    }

}