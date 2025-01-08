package net.darkmeow.irc.utils

import io.netty.channel.Channel
import net.darkmeow.irc.network.AttributeKeys
import net.darkmeow.irc.network.packet.s2c.S2CPacketDisconnect
import net.darkmeow.irc.utils.ChannelUtils.sendPacket
import java.util.*

object ChannelAttrUtils {

    /**
     * 获取连接唯一标识
     *
     * @return UUID
     */
    fun Channel.getUniqueId() = this
        .takeIf { hasAttr(AttributeKeys.UUID) }
        ?.attr(AttributeKeys.UUID)
        ?.get()
        ?: UUID(0L, 0L)

    fun Channel.getCurrentUser() = this
        .takeIf { hasAttr(AttributeKeys.CURRENT_USER) }
        ?.attr(AttributeKeys.CURRENT_USER)
        ?.get()

    fun Channel.setCurrentUser(user: String) = this
        .attr(AttributeKeys.CURRENT_USER)
        .set(user)

    fun Channel.getDevice() = this
        .takeIf { hasAttr(AttributeKeys.DEVICE) }
        ?.attr(AttributeKeys.DEVICE)
        ?.get()
        ?: ""

    /**
     * 踢出当前客户端
     *
     * @param reason 踢出原因
     * @param logout 是否同时退出客户端用户登录状态
     */
    fun Channel.kick(reason: String, logout: Boolean = false) = this
        .apply { sendPacket(S2CPacketDisconnect(reason, logout)) }
        .disconnect()
        .let { true }
}