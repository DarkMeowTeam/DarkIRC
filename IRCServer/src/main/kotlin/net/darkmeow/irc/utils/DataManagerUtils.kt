package net.darkmeow.irc.utils

import io.netty.channel.Channel
import net.darkmeow.irc.data.DataManager
import net.darkmeow.irc.utils.ChannelAttrUtils.getCurrentUser

object DataManagerUtils {
    fun DataManager.getCTXPremium(ctx: Channel) = this.getUserPremium(ctx.getCurrentUser() ?: "Guest")
}