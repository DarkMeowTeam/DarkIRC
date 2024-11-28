package net.darkmeow.irc.network

import net.darkmeow.irc.IRCServer
import net.darkmeow.irc.utils.AESUtils
import net.darkmeow.irc.utils.Log4jProtectUtils.isJndiLdap
import net.darkmeow.irc.utils.LoggerUtils.getAddress
import org.apache.logging.log4j.LogManager
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.*

class NetworkManager(private val base: IRCServer) {
    @JvmField
    val logger = LogManager.getLogger(NetworkManager::class.java)

    @JvmField
    val clients: HashMap<UUID, IRCClientConnection> = hashMapOf()

    fun start() {
        val serverSocket = ServerSocket(base.configManager.configs.port)
        logger.info("IRC 服务器启动成功(监听端口=${base.configManager.configs.port})")

        while (true) {
            val clientSocket = serverSocket.accept()
            handleClient(clientSocket)
        }
    }

    private fun handleClient(clientSocket: Socket) {
        val uuid = UUID.randomUUID()

        clients[uuid] = IRCClientConnection(
            socket = clientSocket,
            writer = PrintWriter(clientSocket.getOutputStream(), true),
            reader =  BufferedReader(InputStreamReader(clientSocket.getInputStream()))
        )

        logger.info("[连接管理] 客户端(uuid=${uuid}, addr=${clientSocket.getAddress()})已连接")

        Thread {
            try {
                var doStop = false
                while (!doStop) {
                    clients[uuid]
                        ?.reader
                        ?.readLine()
                        ?.let { AESUtils.decryptAES(it, base.configManager.configs.key) }
                        ?.let { message ->
                            message
                                .takeIf { !it.isJndiLdap() }
                                ?.also {
                                    logger.info("[$uuid] $it")
                                    broadcast(AESUtils.encryptAES(it, base.configManager.configs.key))
                                } ?: run {
                                    logger.info("[$uuid] 此消息已被过滤")
                                }
                        } ?: run {
                            logger.info("[连接管理] 客户端(uuid=${uuid})无效数据 已强制断开")
                            doStop = true
                        }
                }
                disconnect(uuid)
            } finally {
                // 确保断开连接
                disconnect(uuid)
            }
        }.start()
    }

    private fun disconnect(uuid: UUID) = clients[uuid]
        ?.socket
        ?.also { logger.info("[连接管理] 客户端(uuid=${uuid})已断开") }
        ?.close()
        ?.also { clients.remove(uuid) }
        ?.let { true } ?: false

    /**
     * 向所有客户端广播消息
     *
     * @return 成功广播数
     */
    private fun broadcast(message: String): Int = clients
        .count { (_, client) ->
            try {
                client.writer.apply {
                    println(message)
                    flush()
                }
                true
            } catch (_: Exception) {
                false
            }
        }
}

