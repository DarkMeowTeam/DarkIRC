package net.darkmeow.irc.command.impl

import io.netty.channel.Channel
import net.darkmeow.irc.command.Command
import net.darkmeow.irc.command.CommandManager
import net.darkmeow.irc.network.packet.s2c.S2CPacketDisconnect
import net.darkmeow.irc.network.packet.s2c.S2CPacketUpdateMySessionInfo
import net.darkmeow.irc.utils.ChannelAttrUtils.getCurrentUser
import net.darkmeow.irc.utils.ChannelAttrUtils.getSessionIsInvisible
import net.darkmeow.irc.utils.ChannelAttrUtils.getUniqueId
import net.darkmeow.irc.utils.ChannelAttrUtils.kick
import net.darkmeow.irc.utils.ChannelUtils.sendPacket
import net.darkmeow.irc.utils.ChannelUtils.sendSystemMessage
import net.darkmeow.irc.utils.DataManagerUtils.getCTXPremium
import net.darkmeow.irc.utils.MessageUtils.sendCommandUsage
import net.darkmeow.irc.utils.MessageUtils.sendMessageError

class CommandIgnore: Command("Ignore") {

    override fun handle(manager: CommandManager, channel: Channel, args: MutableList<String>) {
        val user = channel.getCurrentUser() ?: return

        when (if (args.isEmpty()) "" else args[0]) {
            "add" -> {
                if (args.size != 2) {
                    channel.sendCommandUsage("ignore", "add <用户名/服务名>")
                    return
                }
                manager.base.dataManager.getUserdataIgnores(user)
                    .also {
                        if (it.contains(args[1])) {
                            channel.sendSystemMessage("${args[1]} 已经处于屏蔽列表中")
                        } else {
                            it.add(args[1])
                            manager.base.dataManager.setUserdataIgnores(user, it)
                            channel.sendSystemMessage("已屏蔽 ${args[1]} 的消息")
                        }
                    }
            }
            "remove" -> {
                if (args.size != 2) {
                    channel.sendCommandUsage("ignore", "remove <用户名/服务名>")
                    return
                }
                manager.base.dataManager.getUserdataIgnores(user)
                    .also {
                        if (!it.contains(args[1])) {
                            channel.sendSystemMessage("${args[1]} 不处于屏蔽列表中")
                        } else {
                            it.remove(args[1])
                            manager.base.dataManager.setUserdataIgnores(user, it)
                            channel.sendSystemMessage("已取消屏蔽 ${args[1]} 的消息")
                        }
                    }
            }
            "list" -> {
                manager.base.dataManager.getUserdataIgnores(user).also { users ->
                    channel.sendSystemMessage("已屏蔽列表(${users.size}): ${users.joinToString(", ")}")
                }
            }
            else -> channel.sendCommandUsage("ignore", "<add/remove/list> <...>")
        }
    }
}