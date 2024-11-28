package net.darkmeow.irc

import java.io.BufferedReader
import java.io.InputStreamReader

fun main() {
    val client = object : IRCClient() {
        override fun onMessage(message: String) {
            // 处理从服务器接收到的消息
            println(message)
        }
    }

    // 启动客户端并连接到服务器
    client.start("127.0.0.1", 8080, "publicIRCTest123")

    // 启动一个线程来监听标准输入
    Thread {
        val userInputReader = BufferedReader(InputStreamReader(System.`in`))
        var userInput: String?
        while (userInputReader.readLine().also { userInput = it } != null) {
            client.sendMessage(userInput!!)  // 发送用户输入的消息到服务器
        }
    }.start()

    // 示例：发送一条初始消息
    Thread.sleep(1000)
    client.sendMessage("Message")
}
