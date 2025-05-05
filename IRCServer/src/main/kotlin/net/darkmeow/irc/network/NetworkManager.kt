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
import io.netty.handler.timeout.ReadTimeoutHandler
import net.darkmeow.irc.IRCServer
import net.darkmeow.irc.network.handle.frame.NettyVarInt21FrameDecoder
import net.darkmeow.irc.network.handle.frame.NettyVarInt21FrameEncoder
import net.darkmeow.irc.network.handle.packet.NettyPacketDecoder
import net.darkmeow.irc.network.handle.packet.NettyPacketEncoder
import net.darkmeow.irc.network.handles.handshake.HandlePacketEncryptionResponse
import net.darkmeow.irc.network.handles.netty.NettyAddressLogger
import net.darkmeow.irc.network.handles.handshake.HandlePacketHandShake
import net.darkmeow.irc.network.handles.handshake.HandlePacketSignatureResponse
import net.darkmeow.irc.network.handles.login.HandlePacketLogin
import net.darkmeow.irc.network.handles.online.HandlePacketAuthentication
import net.darkmeow.irc.network.handles.online.HandlePacketInputStatus
import net.darkmeow.irc.network.handles.online.HandlePacketKeepAlive
import net.darkmeow.irc.network.handles.online.HandlePacketMessage
import net.darkmeow.irc.network.handles.online.HandlePacketSessionSkin
import net.darkmeow.irc.network.handles.online.HandlePacketSessionState
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
    val clients: MutableMap<UUID, IRCNetworkManagerServer> = Collections.synchronizedMap(hashMapOf())

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
                            val subNetworkManager = IRCNetworkManagerServer(this@NetworkManager)

                            // Network | Proxy Protocol
                            if (base.configManager.configs.ircServer.proxyProtocol) {
                                ch.pipeline().addLast("proxy_protocol", HAProxyMessageDecoder())
                            }

                            ch.pipeline().addLast("netty_address_logger", NettyAddressLogger(subNetworkManager))
                            // 超时断开
                            ch.pipeline().addLast("timeout", ReadTimeoutHandler(IRCNetworkBaseConfig.READ_TIMEOUT_SECOND))
                            // 入站包分片和解码
                            ch.pipeline().addLast("splitter", NettyVarInt21FrameDecoder())
                            ch.pipeline().addLast("decoder", NettyPacketDecoder(EnumPacketDirection.SERVER_BOUND))
                            // 出站包分片和编码
                            ch.pipeline().addLast("prepender", NettyVarInt21FrameEncoder())
                            ch.pipeline().addLast("encoder", NettyPacketEncoder(EnumPacketDirection.CLIENT_BOUND))

                            ch.pipeline().addLast("base", subNetworkManager)
                            ch.pipeline().addLast("handler_hand_shake", HandlePacketHandShake(subNetworkManager))
                            ch.pipeline().addLast("handler_signature_response", HandlePacketSignatureResponse(subNetworkManager))
                            ch.pipeline().addLast("handler_encryption_response", HandlePacketEncryptionResponse(subNetworkManager))
                            ch.pipeline().addLast("handler_login", HandlePacketLogin(this@NetworkManager, subNetworkManager))
                            ch.pipeline().addLast("handler_online_keepalive", HandlePacketKeepAlive(subNetworkManager))
                            ch.pipeline().addLast("handler_online_message", HandlePacketMessage(subNetworkManager))
                            ch.pipeline().addLast("handler_online_input_status", HandlePacketInputStatus(subNetworkManager))
                            ch.pipeline().addLast("handler_online_session_state", HandlePacketSessionState(subNetworkManager))
                            ch.pipeline().addLast("handler_online_session_skin", HandlePacketSessionSkin(subNetworkManager))
                            ch.pipeline().addLast("handler_online_authentication", HandlePacketAuthentication(subNetworkManager))
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
        clients.values.onEach { other ->
            other.kick(reason = "服务器被管理员关闭 请稍后重新连接", logout = false)
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