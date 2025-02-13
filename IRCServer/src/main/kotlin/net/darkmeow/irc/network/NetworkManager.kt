package net.darkmeow.irc.network

import io.netty.bootstrap.ServerBootstrap
import io.netty.buffer.PooledByteBufAllocator
import io.netty.channel.Channel
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.EventLoopGroup
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.codec.haproxy.HAProxyMessageDecoder
import net.darkmeow.irc.IRCServer
import net.darkmeow.irc.network.handles.HandleClientConnection
import net.darkmeow.irc.network.handles.HandleClientEncryption
import net.darkmeow.irc.network.handles.HandlePacketProcess
import net.darkmeow.irc.network.packet.s2c.S2CPacketDisconnect
import net.darkmeow.irc.utils.ChannelAttrUtils.kick
import net.darkmeow.irc.utils.ChannelUtils.sendPacket
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.util.*
import java.util.concurrent.CountDownLatch

class NetworkManager(
    val base: IRCServer
) {
    @JvmField
    val logger: Logger = LogManager.getLogger("Network")

    @JvmField
    val keepAliveManager = NetworkKeepAliveManager(this)
    /**
     * 已连接的客户端
     */
    @JvmField
    val clients: MutableMap<UUID, Channel> = Collections.synchronizedMap(hashMapOf())

    private var serverChannel: Channel? = null

    private var bossGroup: EventLoopGroup? = null
    private var workerGroup: EventLoopGroup? = null

    fun start(): Boolean {
        val latch = CountDownLatch(1)
        Thread {
            runCatching {
                val bootstrap = ServerBootstrap()

                bossGroup = NioEventLoopGroup() // 用于接收客户端连接
                workerGroup = NioEventLoopGroup() // 用于处理连接的 I/O 操作

                bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel::class.java)
                    .childHandler(object : ChannelInitializer<SocketChannel>() {
                        override fun initChannel(ch: SocketChannel) {
                            // Network | Proxy Protocol
                            if (base.configManager.configs.ircServer.proxyProtocol) {
                                ch.pipeline().addLast("ProxyProtocol", HAProxyMessageDecoder())
                            }

                            // Base | 客户端连接状态距离
                            ch.pipeline().addLast("BaseConnection", HandleClientConnection(this@NetworkManager))
                            // Base | 客户端传输加密
                            ch.pipeline().addLast("BaseEncryption", HandleClientEncryption(this@NetworkManager))

                            ch.pipeline().addLast("Handler", HandlePacketProcess(this@NetworkManager))
                        }
                    })
                    .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .childOption(ChannelOption.SO_REUSEADDR, true)

                val future = bootstrap.bind(base.configManager.configs.ircServer.host, base.configManager.configs.ircServer.port).sync()
                serverChannel = future.channel()

                logger.info("监听于 ${base.configManager.configs.ircServer.host}:${base.configManager.configs.ircServer.port}")

                latch.countDown()

                keepAliveManager.start()
                future.channel().closeFuture().sync()
                keepAliveManager.stop()

                logger.info("已关闭")
            }
                .onFailure { t ->
                    logger.error("启动时发生异常", t)
                    Thread.currentThread().interrupt()

                    latch.countDown()
                }
        }.start()

        return serverChannel?.isActive ?: false
    }


    /**
     * 停止服务端工作
     *
     * @return 是否成功
     */
    fun stop() = runCatching {
        clients.values.onEach { channel ->
            runCatching {
                channel.kick(reason = "服务器被管理员关闭 请稍后重新连接", logout = false)
            }
        }

        serverChannel?.close()?.sync() // 关闭 Channel
        bossGroup?.shutdownGracefully() // 关闭 bossGroup
        workerGroup?.shutdownGracefully() // 关闭 workerGroup
    }
        .onFailure { t ->
            logger.error("停止时发生异常", t)
            Thread.currentThread().interrupt()
        }
        .isSuccess
}