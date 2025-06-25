package net.darkmeow.irc.network

import io.netty.channel.ChannelHandlerContext
import io.netty.util.concurrent.Future
import io.netty.util.concurrent.GenericFutureListener
import net.darkmeow.irc.data.DataClientBrand
import net.darkmeow.irc.data.DataSkin
import net.darkmeow.irc.data.DataUserState
import net.darkmeow.irc.data.enmus.EnumUserPremium
import net.darkmeow.irc.data.input.DataSessionInputStatusBase
import net.darkmeow.irc.data.sync.DataSyncInputStatus
import net.darkmeow.irc.network.packet.handshake.s2c.S2CPacketDenyHandShake
import net.darkmeow.irc.network.packet.handshake.s2c.S2CPacketServerInfo
import net.darkmeow.irc.network.packet.login.s2c.S2CPacketLoginFailed
import net.darkmeow.irc.network.packet.online.s2c.S2CPacketDisconnect
import net.darkmeow.irc.network.packet.online.s2c.S2CPacketSystemMessage
import java.security.KeyPair
import java.util.*


class IRCNetworkManagerServer(val bossNetworkManager: NetworkManager): IRCNetworkManager() {
    /**
     * 会话唯一标识
     */
    lateinit var sessionId: UUID
    /**
     * 如果服务端启用加密
     * 这里是存储密钥对的地方 (每个客户端都不同)
     */
    lateinit var keyPair: KeyPair
    /**
     * 如果服务端启用签名验证
     * 这里是需要客户端签名的一次性代码
     */
    lateinit var signatureData: ByteArray
    /**
     * 客户端 IP 地址
     */
    var address: String = "unknown"
    /**
     * 协议版本
     */
    var protocolVersion: Int = 0
    /**
     * 连接客户端信息
     */
    lateinit var brand: DataClientBrand
    /**
     * 硬件唯一标识
     */
    lateinit var hardWareUniqueId: String

    /**
     * 当前登录用户名称
     */
    var user: String = ""
    /**
     * 当前会话对于登录用户的权限级别
     */
    var userPremium: EnumUserPremium = EnumUserPremium.BANNED
    /**
     * 当前登录使用的 Token
     */
    var currentToken: String? = null
    /**
     * 会话状态信息 (来自客户端上报)
     */
    var sessionState: DataUserState = DataUserState.EMPTY
    /**
     * 会话角色绑定皮肤信息 (来自客户端上报)
     */
    var sessionSkin: DataSkin? = null
    /**
     * 会话是否开启隐身
     */
    var currentIsInvisible: Boolean = false

    var lastKeepAlive: Long
        get() {
            return channel?.attr(AttributeKeys.LATEST_KEEPALIVE)?.get() ?: System.currentTimeMillis()
        }
        set(value) {
            channel?.attr(AttributeKeys.LATEST_KEEPALIVE)?.set(value)
        }

    var inputStatus: DataSessionInputStatusBase? = null
    val syncInputStatus = DataSyncInputStatus()

    /**
     * 是否已登录
     * 只有已登录的会话才有 sessionId
     */
    fun isLogin() = user.isNotEmpty()

    fun updateLastKeepAlive() {
        lastKeepAlive = System.currentTimeMillis()
    }

    fun sendSystemMessage(message: String) {
        sendPacket(S2CPacketSystemMessage(message, UUID.randomUUID()))
    }

    /**
     * 发送服务端信息
     * 只能在握手阶段调用
     */
    fun sendServerInfo() {
        sendPacket(S2CPacketServerInfo(System.currentTimeMillis(), bossNetworkManager.base.configManager.serverKey))
    }

    /**
     * 将客户端踢下线
     *
     * @param reason 原因
     * @param logout 是否登出 (仅在 ONLINE 阶段生效)
     */
    fun kick(reason: String = "", logout: Boolean = false) {
        val future = GenericFutureListener<Future<Void>> { future -> close() }

        when (connectionState) {
            EnumConnectionState.HANDSHAKING -> sendPacket(S2CPacketDenyHandShake(reason), future)
            EnumConnectionState.LOGIN -> sendPacket(S2CPacketLoginFailed(reason, logout), future)
            EnumConnectionState.ONLINE -> sendPacket(S2CPacketDisconnect(logout, reason), future)
            else -> { }
        }
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, e: Throwable) {
        close()
    }

    override fun channelInactive(ctx: ChannelHandlerContext) {
        if (isLogin()) {
            bossNetworkManager.clients.remove(sessionId)
            bossNetworkManager.logger.info("[-] $user")
        }
        super.channelInactive(ctx)
    }

}