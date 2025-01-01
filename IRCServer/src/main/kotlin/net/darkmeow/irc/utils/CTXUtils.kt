package net.darkmeow.irc.utils

import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import net.darkmeow.irc.data.DataManager
import net.darkmeow.irc.network.AttributeKeys

object CTXUtils {
    fun ChannelHandlerContext.getCurrentUser() = this.channel().getCurrentUser()
    fun ChannelHandlerContext.setCurrentUser(user: String) = this.channel().setCurrentUser(user)
    fun ChannelHandlerContext.clearCurrentUser() = this.channel().clearCurrentUser()

    fun Channel.getCurrentUser() = this
        .takeIf { hasAttr(AttributeKeys.CURRENT_USER) }
        ?.attr(AttributeKeys.CURRENT_USER)
        ?.get()

    fun Channel.setCurrentUser(user: String) = this
        .attr(AttributeKeys.CURRENT_USER)
        .set(user)

    fun Channel.clearCurrentUser() = this
        .takeIf { hasAttr(AttributeKeys.CURRENT_USER) }
        ?.attr(AttributeKeys.CURRENT_USER)
        ?.remove()
        ?.let { true } ?: false
}