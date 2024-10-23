package net.darkmeow.irc

import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

open class IRCClient {
    private lateinit var writer: PrintWriter

    private var socket : Socket = Socket()

    fun start(host: String, port: Int) {
        socket = Socket(host, port)

        try {
            val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
            writer = PrintWriter(socket.getOutputStream(), true)

            // 启动一个线程来接收消息
            Thread {
                try {
                    var message: String?
                    while (reader.readLine().also { message = it } != null) {
                        message?.let { onMessage(URLDecoder.decode(it, StandardCharsets.UTF_8.toString())) }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    socket.close()
                }
            }.start()

            // 主线程用于读取用户输入并发送到服务器
            val userInputReader = BufferedReader(InputStreamReader(System.`in`))
            var userInput: String?
            while (userInputReader.readLine().also { userInput = it } != null) {
                sendMessage(userInput!!) // 调用公开的发送消息方法
            }

        } catch (e: Exception) {
            e.printStackTrace()
            onClosed()
        }
    }

    fun close() {
        try {
            socket.close()
        } catch (_: Throwable) { }
    }

    // 公开的发送消息方法
    fun sendMessage(message: String) {
        if (::writer.isInitialized) {
            writer.println(URLEncoder.encode(message, StandardCharsets.UTF_8.toString()))
            writer.flush()
        } else {
            println("Error: Connection is not established.")
        }
    }

    open fun onMessage(message: String) { }

    open fun onClosed() { }


}
