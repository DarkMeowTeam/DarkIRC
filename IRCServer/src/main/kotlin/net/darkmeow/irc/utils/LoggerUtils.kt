package net.darkmeow.irc.utils

import java.net.Socket

object LoggerUtils {
    fun Socket.getAddress() = inetAddress.hostAddress + ":" + port
}