 package net.darkmeow.irc.network

import net.darkmeow.irc.network.packet.s2c.S2CPacketKeepAlive
import net.darkmeow.irc.utils.ChannelUtils.sendPacket
import java.util.*
import kotlin.concurrent.timerTask

 class NetworkKeepAliveManager (
    val manager: NetworkManager
) {
     companion object {
         const val KEEPALIVE_TIMEOUT = 15000
     }
    @JvmField
    var id = 0L

    val timer = Timer()

    fun start() {
        timer.schedule(timerTask {
            runKeepAlive()
        }, 5000, 2000)
    }

     fun stop() {
         timer.cancel()
     }

    fun runKeepAlive() {
        manager.clients.values
            .filter { it.hasAttr(AttributeKeys.DEVICE) }
            .forEach { channel ->
                // 发送 心跳包
                channel.sendPacket(S2CPacketKeepAlive(id))

                // 超时
                if (channel.attr(AttributeKeys.LATEST_KEEPALIVE).get() + KEEPALIVE_TIMEOUT < System.currentTimeMillis() ) {
                    channel.close()
                }
            }
            .also {
                id++
            }
    }
}