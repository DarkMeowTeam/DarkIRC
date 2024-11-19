package net.darkmeow.irc

import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

open class IRCClient {
    object AESUtils {
        fun encryptAES(input: String, key: String): String {
            val secretKey = SecretKeySpec(key.toByteArray(), "AES")
            val cipher = Cipher.getInstance("AES")
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)
            return Base64.getEncoder().encodeToString(cipher.doFinal(input.toByteArray()))
        }

        fun decryptAES(input: String, key: String): String? {
            return try {
                val cipher = Cipher.getInstance("AES").apply {
                    val secretKey = SecretKeySpec(key.toByteArray(), "AES")
                    init(Cipher.DECRYPT_MODE, secretKey)
                }

                Base64.getDecoder().decode(input).let { decodedBytes ->
                    String(cipher.doFinal(decodedBytes))
                }
            } catch (e: Exception) {
                null
            }
        }

    }

    private lateinit var writer: PrintWriter

    private var socket : Socket = Socket()

    var key = ""

    fun start(host: String, port: Int, key: String) {
        this.key = key
        socket = Socket(host, port)

        try {
            val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
            writer = PrintWriter(socket.getOutputStream(), true)

            // 启动一个线程来接收消息
            Thread {
                try {
                    var message: String?
                    while (reader.readLine().also { message = it } != null) {
                        message?.let { onMessage(AESUtils.decryptAES(it, this.key) ?: return@let) }
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
            writer.println(AESUtils.encryptAES(message, key))
            writer.flush()
        } else {
            println("Error: Connection is not established.")
        }
    }

    open fun onMessage(message: String) { }

    open fun onClosed() { }


}
