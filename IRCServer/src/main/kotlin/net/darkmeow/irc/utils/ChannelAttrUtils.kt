package net.darkmeow.irc.utils

import io.netty.channel.Channel
import net.darkmeow.irc.data.DataSessionInfo
import net.darkmeow.irc.data.DataSessionOptions
import net.darkmeow.irc.network.AttributeKeys
import net.darkmeow.irc.network.packet.s2c.S2CPacketDisconnect
import net.darkmeow.irc.utils.ChannelUtils.sendPacket
import java.util.*

object ChannelAttrUtils {


    fun Channel.getAddress() = this
        .takeIf { hasAttr(AttributeKeys.ADDRESS) }
        ?.attr(AttributeKeys.ADDRESS)
        ?.get()
        ?: "unknown"

    fun Channel.setAddress(address: String) = this
        .attr(AttributeKeys.ADDRESS)
        .set(address)

    fun Channel.getLatestKeepAlive() = this
        .takeIf { hasAttr(AttributeKeys.LATEST_KEEPALIVE) }
        ?.attr(AttributeKeys.LATEST_KEEPALIVE)
        ?.get()
        ?: 0L

    fun Channel.setLatestKeepAlive(time: Long) = this
        .attr(AttributeKeys.LATEST_KEEPALIVE)
        .set(time)


    fun Channel.getProtocolVersion() = this
        .takeIf { hasAttr(AttributeKeys.PROTOCOL) }
        ?.attr(AttributeKeys.PROTOCOL)
        ?.get()
        ?: 0

    fun Channel.setProtocolVersion(version: Int) = this
        .attr(AttributeKeys.PROTOCOL)
        .set(version)
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

    fun Channel.setUniqueId(id: UUID) = this
        .attr(AttributeKeys.UUID)
        .set(id)

    fun Channel.getCurrentUser() = this
        .takeIf { hasAttr(AttributeKeys.CURRENT_USER) }
        ?.attr(AttributeKeys.CURRENT_USER)
        ?.get()

    fun Channel.setCurrentUser(user: String) = this
        .attr(AttributeKeys.CURRENT_USER)
        .set(user)

    fun Channel.getCurrentToken() = this
        .takeIf { hasAttr(AttributeKeys.CURRENT_TOKEN) }
        ?.attr(AttributeKeys.CURRENT_TOKEN)
        ?.get()

    fun Channel.setCurrentToken(user: String) = this
        .attr(AttributeKeys.CURRENT_TOKEN)
        .set(user)

    fun Channel.getDevice() = this
        .takeIf { hasAttr(AttributeKeys.DEVICE) }
        ?.attr(AttributeKeys.DEVICE)
        ?.get()
        ?: ""

    fun Channel.setDevice(device: String) = this
        .attr(AttributeKeys.DEVICE)
        .set(device)

    fun Channel.getSessionInfo() = this
        .takeIf { hasAttr(AttributeKeys.SESSION_INFO) }
        ?.attr(AttributeKeys.SESSION_INFO)
        ?.get()
        ?: DataSessionInfo.EMPTY

    fun Channel.setSessionInfo(info: DataSessionInfo) = this
        .attr(AttributeKeys.SESSION_INFO)
        .set(info)

    fun Channel.getSessionOptions() = this
        .takeIf { hasAttr(AttributeKeys.SESSION_OPTIONS) }
        ?.attr(AttributeKeys.SESSION_OPTIONS)
        ?.get()
        ?: DataSessionOptions.EMPTY

    fun Channel.setSessionOptions(info: DataSessionOptions) = this
        .attr(AttributeKeys.SESSION_OPTIONS)
        .set(info)


    fun Channel.getSessionIsInvisible() = this
        .takeIf { hasAttr(AttributeKeys.SESSION_IS_INVISIBLE) }
        ?.attr(AttributeKeys.SESSION_IS_INVISIBLE)
        ?.get()
        ?: false

    fun Channel.setSessionIsInvisible(flag: Boolean) = this
        .attr(AttributeKeys.SESSION_IS_INVISIBLE)
        .set(flag)

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