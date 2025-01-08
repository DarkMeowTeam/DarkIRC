package net.darkmeow.irc.command.impl

import io.netty.channel.Channel
import net.darkmeow.irc.command.Command
import net.darkmeow.irc.command.CommandManager
import net.darkmeow.irc.network.packet.s2c.S2CPacketUpdateMyInfo
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
                if (manager.base.dataManager.getCTXPremium(channel) != S2CPacketUpdateMyInfo.Premium.SUPER_ADMIN) {
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
                if (manager.base.dataManager.getCTXPremium(channel) != S2CPacketUpdateMyInfo.Premium.SUPER_ADMIN) {
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
                if (manager.base.dataManager.getCTXPremium(channel) != S2CPacketUpdateMyInfo.Premium.SUPER_ADMIN) {
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
                if (manager.base.dataManager.getCTXPremium(channel) != S2CPacketUpdateMyInfo.Premium.SUPER_ADMIN) {
                    channel.sendMessageError("当前登录用户无权限执行该命令")
                    return
                }
                manager.base.dataManager.getClients().also { users ->
                    channel.sendSystemMessage("客户端列表(${users.size}): ${users.joinToString(", ")}")
                }
            }
            else -> channel.sendCommandUsage("clients", "<create/delete/version/list> <...>")
        }

    }

}