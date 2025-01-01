package net.darkmeow.irc.command.impl

import io.netty.channel.ChannelHandlerContext
import net.darkmeow.irc.command.Command
import net.darkmeow.irc.command.CommandManager
import net.darkmeow.irc.network.packet.s2c.S2CPacketUpdateMyInfo
import net.darkmeow.irc.utils.CTXUtils.clearCurrentUser
import net.darkmeow.irc.utils.CTXUtils.getCurrentUser
import net.darkmeow.irc.utils.ChannelUtils.sendPacket
import net.darkmeow.irc.utils.ChannelUtils.sendSystemMessage
import net.darkmeow.irc.utils.MessageUtils.sendCommandUsage

class CommandChangePassword: Command("ChangePassword", "CP") {

    override fun handle(manager: CommandManager, ctx: ChannelHandlerContext, args: MutableList<String>) {
        if (args.size != 1) {
            ctx.sendCommandUsage("cp", "<新密码>")
            return
        }
        manager.base.dataManager.setUserPassword(ctx.getCurrentUser() ?: throw NullPointerException(), args[0])

        ctx.sendSystemMessage("密码修改成功,请重新登录")

        manager.base.networkManager.clients.values
            .filter { channel -> channel.getCurrentUser() == ctx.getCurrentUser() }
            .onEach { channel ->
                channel.sendPacket(S2CPacketUpdateMyInfo("", "", S2CPacketUpdateMyInfo.Premium.GUEST))
                channel.clearCurrentUser()
            }

    }
}