package net.darkmeow.irc.network

import java.io.BufferedReader
import java.io.PrintWriter
import java.net.Socket

data class IRCClientConnection(
    val socket: Socket,
    val writer: PrintWriter,
    val reader: BufferedReader
)
