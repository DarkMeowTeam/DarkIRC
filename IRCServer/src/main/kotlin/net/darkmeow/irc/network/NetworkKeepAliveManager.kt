 package net.darkmeow.irc.network

import net.darkmeow.irc.IRCLib
import net.darkmeow.irc.network.packet.s2c.S2CPacketKeepAlive
import net.darkmeow.irc.utils.ChannelAttrUtils.getLatestKeepAlive
import net.darkmeow.irc.utils.ChannelAttrUtils.getProtocolVersion
import net.darkmeow.irc.utils.ChannelUtils.sendPacket
import net.darkmeow.irc.utils.ChannelUtils.sendSystemMessage
import java.util.*
import kotlin.concurrent.timerTask

 class NetworkKeepAliveManager (
    val manager: NetworkManager
) {
     companion object {
         /**
          * 心跳包发送间隔
          */
         const val KEEPALIVE_DELAY = 5000L
         /**
          * 心跳包回应超时
          */
         const val KEEPALIVE_TIMEOUT = 15000L
     }


    @JvmField
    var id = 0L

     @JvmField
    val timer = Timer()

    fun start() {
        timer.schedule(timerTask {
            runKeepAlive()
            runLegacyProtocolWarn()
        }, KEEPALIVE_DELAY, KEEPALIVE_DELAY)
    }

     fun stop() {
         timer.cancel()
     }

     fun runKeepAlive() {
         manager.clients.values
             .onEach { channel ->
                 runCatching {
                     channel
                         .takeIf { it.getLatestKeepAlive() + KEEPALIVE_TIMEOUT < System.currentTimeMillis() }
                         ?.close()
                 }
             }
             .filter { it.hasAttr(AttributeKeys.DEVICE) }
             .onEach { channel ->
                 runCatching {
                     channel.sendPacket(S2CPacketKeepAlive(id))
                 }
             }
             .also {
                 id++
             }
     }


     var legacyProtocolWarnIndex = 0

     fun runLegacyProtocolWarn() {
         legacyProtocolWarnIndex++

         manager.clients.values
             .takeIf { legacyProtocolWarnIndex >= 12 }
             ?.also { legacyProtocolWarnIndex = 0 }
             ?.forEach { channel ->
                 runCatching {
                     if (channel.getProtocolVersion() < IRCLib.PROTOCOL_VERSION) {
                         channel.sendSystemMessage("您的 IRC 客户端协议版本已过时, 过时的版本可能无法查看 IRC 内其他玩家信息.")
                     }
                 }
             }

     }
}