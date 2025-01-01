package net.darkmeow.irc.utils

import io.netty.channel.ChannelHandlerContext
import net.darkmeow.irc.data.DataManager
import net.darkmeow.irc.utils.CTXUtils.getCurrentUser

object DataManagerUtils {
    fun DataManager.getCTXPremium(ctx: ChannelHandlerContext) = this.getUserPremium(ctx.getCurrentUser() ?: "Guest")
}