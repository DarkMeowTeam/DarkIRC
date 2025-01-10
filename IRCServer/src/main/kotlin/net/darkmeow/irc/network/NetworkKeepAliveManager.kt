 package net.darkmeow.irc.network

import net.darkmeow.irc.network.packet.s2c.S2CPacketKeepAlive
import net.darkmeow.irc.utils.ChannelUtils.sendPacket
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
        }, KEEPALIVE_DELAY, KEEPALIVE_DELAY)
    }

     fun stop() {
         timer.cancel()
     }

     fun runKeepAlive() {
         manager.clients.values
             .filter { it.hasAttr(AttributeKeys.DEVICE) }
             .forEach { channel ->
                 runCatching {
                     // 发送 心跳包
                     channel.sendPacket(S2CPacketKeepAlive(id))

                     // 超时
                     if (channel.attr(AttributeKeys.LATEST_KEEPALIVE).get() + KEEPALIVE_TIMEOUT < System.currentTimeMillis()) {
                         channel.close()
                     }
                 }
             }
             .also {
                 id++
             }
     }
}