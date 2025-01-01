package net.darkmeow.irc.command.impl

import io.netty.channel.ChannelHandlerContext
import net.darkmeow.irc.command.Command
import net.darkmeow.irc.command.CommandManager
import net.darkmeow.irc.network.AttributeKeys
import net.darkmeow.irc.network.packet.s2c.S2CPacketDisconnect
import net.darkmeow.irc.network.packet.s2c.S2CPacketUpdateMyInfo
import net.darkmeow.irc.utils.CTXUtils.getCurrentUser
import net.darkmeow.irc.utils.ChannelUtils.sendPacket
import net.darkmeow.irc.utils.ChannelUtils.sendSystemMessage
import net.darkmeow.irc.utils.DataManagerUtils.getCTXPremium
import net.darkmeow.irc.utils.MessageUtils.sendCommandUsage
import net.darkmeow.irc.utils.MessageUtils.sendMessageError

class CommandUsers: Command("Users") {

    override fun handle(manager: CommandManager, ctx: ChannelHandlerContext, args: MutableList<String>) {
        when (if (args.isEmpty()) "" else args[0]) {
            "create" -> {
                if (args.size != 5) {
                    ctx.sendCommandUsage("users", "create <用户名> <密码> <初始头衔> <等级(${S2CPacketUpdateMyInfo.Premium.entries.joinToString (",") { it.name }})>")
                    return
                }
                if (manager.base.dataManager.getCTXPremium(ctx).ordinal < S2CPacketUpdateMyInfo.Premium.ADMIN.ordinal) {
                    ctx.sendMessageError("当前登录用户无权限执行该命令")
                    return
                }
                manager.base.dataManager.createUser(args[1], args[2], args[3], S2CPacketUpdateMyInfo.Premium.valueOf(args[4]))
                ctx.sendSystemMessage("成功创建用户 ${args[1]}")
            }
            "delete" -> {
                if (args.size != 2) {
                    ctx.sendCommandUsage("users", "delete <用户名>")
                    return
                }
                if (manager.base.dataManager.getCTXPremium(ctx).ordinal < S2CPacketUpdateMyInfo.Premium.ADMIN.ordinal) {
                    ctx.sendMessageError("当前登录用户无权限执行该命令")
                    return
                }
                if (manager.base.dataManager.userExist(args[1])) {
                    // 登出该用户的在线客户端
                    manager.base.networkManager.clients
                        .filter { (_, channel) ->
                            channel.getCurrentUser() == args[1]
                        }
                        .onEach { (_, channel) ->
                            channel.sendPacket(
                                S2CPacketUpdateMyInfo(
                                    "",
                                    "",
                                    S2CPacketUpdateMyInfo.Premium.GUEST
                                )
                            )
                            channel.attr(AttributeKeys.CURRENT_USER).remove()
                        }

                    manager.base.dataManager.deleteUser(args[1])
                    ctx.sendSystemMessage("成功删除用户 ${args[1]}")
                } else {
                    ctx.sendSystemMessage("用户 ${args[1]} 不存在")
                }
            }
            "rank" -> {
                if (args.size != 3) {
                    ctx.sendCommandUsage("users", "rank <用户名> <新头衔>")
                    return
                }
                if (manager.base.dataManager.getCTXPremium(ctx).ordinal < S2CPacketUpdateMyInfo.Premium.ADMIN.ordinal) {
                    ctx.sendMessageError("当前登录用户无权限执行该命令")
                    return
                }
                if (manager.base.dataManager.userExist(args[1])) {
                    val premium = manager.base.dataManager.getUserPremium(args[1])

                    manager.base.dataManager.setUserRank(args[1], args[2])

                    // 更新该用户客户端数据
                    manager.base.networkManager.clients
                        .filter { (_, channel) ->
                            channel.getCurrentUser() == args[1]
                        }
                        .onEach { (_, channel) ->
                            channel.sendPacket(
                                S2CPacketUpdateMyInfo(
                                    args[1],
                                    args[2],
                                    premium
                                )
                            )
                        }

                    ctx.sendSystemMessage("成功设置用户 ${args[1]} 的头衔为 ${args[2]}")
                } else {
                    ctx.sendSystemMessage("用户 ${args[1]} 不存在")
                }
            }
            "premium" -> {
                if (args.size != 3) {
                    ctx.sendCommandUsage("users", "premium <用户名> <等级(${S2CPacketUpdateMyInfo.Premium.entries.joinToString (",") { it.name }})>")
                    return
                }
                if (manager.base.dataManager.getCTXPremium(ctx).ordinal < S2CPacketUpdateMyInfo.Premium.ADMIN.ordinal) {
                    ctx.sendMessageError("当前登录用户无权限执行该命令")
                    return
                }
                if (manager.base.dataManager.userExist(args[1])) {
                    val rank = manager.base.dataManager.getUserRank(args[1])

                    manager.base.dataManager.setUserPremium(args[1],  S2CPacketUpdateMyInfo.Premium.valueOf(args[2]))

                    // 更新该用户客户端数据
                    manager.base.networkManager.clients
                        .filter { (_, channel) ->
                            channel.getCurrentUser() == args[1]
                        }
                        .onEach { (_, channel) ->
                            channel.sendPacket(
                                S2CPacketUpdateMyInfo(
                                    args[1],
                                    rank,
                                    S2CPacketUpdateMyInfo.Premium.valueOf(args[2])
                                )
                            )
                        }

                    ctx.sendSystemMessage("成功设置用户 ${args[1]} 的等级为 ${ S2CPacketUpdateMyInfo.Premium.valueOf(args[2])}")
                } else {
                    ctx.sendSystemMessage("用户 ${args[1]} 不存在")
                }
            }
            "kick" -> {
                if (args.size != 3) {
                    ctx.sendCommandUsage("users", "kick <用户名> <原因>")
                    return
                }
                if (manager.base.dataManager.getCTXPremium(ctx).ordinal < S2CPacketUpdateMyInfo.Premium.ADMIN.ordinal) {
                    ctx.sendMessageError("当前登录用户无权限执行该命令")
                    return
                }
                if (manager.base.dataManager.userExist(args[1])) {
                    var count = 0

                    // 更新该用户客户端数据
                    manager.base.networkManager.clients
                        .filter { (_, channel) ->
                            channel.getCurrentUser() == args[1]
                        }
                        .onEach { (_, channel) ->
                            channel.sendPacket(S2CPacketDisconnect(args[2]))
                            channel.disconnect()

                            count++
                        }

                    if (count == 0) {
                        ctx.sendSystemMessage("用户 ${args[1]} 不在线")
                    } else {
                        ctx.sendSystemMessage("用户 ${args[1]} 已被踢出 IRC 服务器")
                    }
                } else {
                    ctx.sendSystemMessage("用户 ${args[1]} 不存在")
                }
            }
            else -> ctx.sendCommandUsage("users", "<create/delete/rank/premium/kick> <...>")
        }
    }
}