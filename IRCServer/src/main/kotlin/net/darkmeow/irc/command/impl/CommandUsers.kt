package net.darkmeow.irc.command.impl

import net.darkmeow.irc.command.Command
import net.darkmeow.irc.command.CommandManager
import net.darkmeow.irc.data.enmus.EnumUserPremium
import net.darkmeow.irc.network.IRCNetworkManagerServer
import net.darkmeow.irc.network.packet.online.s2c.S2CPacketUpdateMyProfile
import net.darkmeow.irc.utils.MessageUtils.sendCommandUsage
import net.darkmeow.irc.utils.MessageUtils.sendMessageError

class CommandUsers: Command("Users") {

    override fun handle(manager: CommandManager, connection: IRCNetworkManagerServer, args: MutableList<String>) {
        when (if (args.isEmpty()) "" else args[0]) {
            "create" -> {
                if (args.size != 5) {
                    connection.sendCommandUsage("users", "create <用户名> <密码> <初始头衔> <等级(${EnumUserPremium.entries.joinToString (",") { it.name }})>")
                    return
                }
                if (connection.userPremium.ordinal < EnumUserPremium.ADMIN.ordinal) {
                    connection.sendMessageError("当前登录用户无权限执行该命令")
                    return
                }
                manager.base.dataManager.createUser(args[1], args[2], args[3], EnumUserPremium.valueOf(args[4]))
                connection.sendSystemMessage("成功创建用户 ${args[1]}")
            }
            "delete" -> {
                if (args.size != 2) {
                    connection.sendCommandUsage("users", "delete <用户名>")
                    return
                }
                if (connection.userPremium.ordinal < EnumUserPremium.ADMIN.ordinal) {
                    connection.sendMessageError("当前登录用户无权限执行该命令")
                    return
                }
                if (manager.base.dataManager.userExist(args[1])) {
                    // 登出该用户的在线客户端
                    manager.base.networkManager.clients.values
                        .filter { other -> other.user == args[1] }
                        .onEach { other -> other.disconnect(reason = "账号被管理员删除", logout = true) }

                    manager.base.dataManager.deleteSessionByUser(args[1])
                    manager.base.dataManager.deleteUser(args[1])
                    connection.sendSystemMessage("成功删除用户 ${args[1]}")
                } else {
                    connection.sendSystemMessage("用户 ${args[1]} 不存在")
                }
            }
            "premium" -> {
                if (args.size != 3) {
                    connection.sendCommandUsage("users", "premium <用户名> <权限级别(${EnumUserPremium.entries.joinToString (",") { it.name }})>")
                    return
                }
                if (connection.userPremium.ordinal < EnumUserPremium.ADMIN.ordinal) {
                    connection.sendMessageError("当前登录用户无权限执行该命令")
                    return
                }
                if (manager.base.dataManager.userExist(args[1])) {
                    runCatching { EnumUserPremium.valueOf(args[2].uppercase()) }
                        .getOrNull()
                        ?.also { newPremium ->
                            manager.base.dataManager.setUserPremium(args[1], newPremium)

                            // 更新该用户客户端数据
                            manager.base.networkManager.clients.values
                                .filter { other -> other.user == args[1] }
                                .onEach { other ->
                                    other.userPremium = newPremium
                                    other.sendPacket(S2CPacketUpdateMyProfile(other.user, newPremium, other.currentIsInvisible))
                                }

                            connection.sendSystemMessage("成功设置用户 ${args[1]} 的等级为 $newPremium")
                        }
                        ?: also {
                            connection.sendSystemMessage("权限级别 ${args[2]} 不存在")
                        }
                } else {
                    connection.sendSystemMessage("用户 ${args[1]} 不存在")
                }
            }
            "kick" -> {
                if (args.size != 3) {
                    connection.sendCommandUsage("users", "kick <用户名> <原因>")
                    return
                }
                if (connection.userPremium.ordinal < EnumUserPremium.ADMIN.ordinal) {
                    connection.sendMessageError("当前登录用户无权限执行该命令")
                    return
                }
                if (manager.base.dataManager.userExist(args[1]) || args[1].length == 36) {
                   // 更新该用户客户端数据
                    manager.base.networkManager.clients.values
                        .filter { other -> other.user == args[1] || other.sessionId.toString() == args[1] }
                        .onEach { other -> other.disconnect(reason = args[2], logout = false) }
                        .also { sessions ->
                            if (sessions.isEmpty()) {
                                connection.sendSystemMessage("用户 ${args[1]} 不在线")
                            } else {
                                connection.sendSystemMessage("用户 ${args[1]} 已被踢出 IRC 服务器")
                            }
                        }
                } else {
                    connection.sendSystemMessage("用户 ${args[1]} 不存在")
                }
            }
            "list" -> {
                if (connection.userPremium.ordinal < EnumUserPremium.ADMIN.ordinal) {
                    connection.sendMessageError("当前登录用户无权限执行该命令")
                    return
                }
                manager.base.dataManager.getUsers().also { users ->
                    connection.sendSystemMessage("用户列表(${users.size}): ${users.joinToString(", ")}")
                }
            }
            else -> connection.sendCommandUsage("users", "<create/delete/rank/premium/kick/list> <...>")
        }
    }
}