package net.darkmeow.irc.network

import net.darkmeow.irc.IRCServer
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
                logger.debug("[连接管理] 客户端(uuid={})开始监听数据", uuid)
                while (!doStop) {
                    clients[uuid]
                        ?.reader
                        ?.readLine()
                        ?.let { URLDecoder.decode(it, StandardCharsets.UTF_8.toString()) }
                        // ${jndi:ldap://example.com/a} 发现此字符串立刻断开连接 (恶意字符)
                        ?.let { message ->
                            message
                                .takeIf { !it.isJndiLdap() }
                                ?.takeIf { it.length <= 200 }
                                ?.also {
                                    logger.info("[$uuid] $it")
                                    broadcast(URLEncoder.encode(it, StandardCharsets.UTF_8.toString()))
                                } ?: run {
                                    logger.info("[$uuid] 此消息已被过滤")
                                }
                        } ?: run {
                            doStop = true
                            logger.debug("[连接管理] 客户端(uuid={})停止监听数据", uuid)
                        }
                }
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

