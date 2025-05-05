 package net.darkmeow.irc.network

import net.darkmeow.irc.network.packet.online.s2c.S2CPacketKeepAlive
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
         manager.clients.values.onEach { other ->
             other.sendPacket(S2CPacketKeepAlive(id))

             if (other.lastKeepAlive + KEEPALIVE_TIMEOUT < System.currentTimeMillis()) {
                 other.kick(reason = "心跳包回应超时", logout = false)
             }
         }
         id++
     }
}