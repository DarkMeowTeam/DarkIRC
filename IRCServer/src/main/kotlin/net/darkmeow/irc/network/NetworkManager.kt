package net.darkmeow.irc.network

import io.netty.bootstrap.ServerBootstrap
import io.netty.buffer.PooledByteBufAllocator
import io.netty.channel.Channel
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.MultiThreadIoEventLoopGroup
import io.netty.channel.nio.NioIoHandler
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

class NetworkManager(val base: IRCServer) {

    companion object {
        val eventLoopGroup = MultiThreadIoEventLoopGroup(NioIoHandler.newFactory())
    }

    @JvmField
    val logger: Logger = LogManager.getLogger("Network")

    @JvmField
    val keepAliveManager = NetworkKeepAliveManager(this)
    /**
     * 已连接的客户端
     */
    @JvmField
    val clients: MutableMap<UUID, IRCNetworkManagerServer> = Collections.synchronizedMap(hashMapOf())

    lateinit var serverChannel: Channel

    fun start() {
        ServerBootstrap()
            .channel(NioServerSocketChannel::class.java)
            .childHandler(object : ChannelInitializer<Channel>() {
                override fun initChannel(ch: Channel) {
                    ch.applyHandle()
                }
            })
            .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
            .childOption(ChannelOption.SO_REUSEADDR, true)
            .group(eventLoopGroup)
            .bind(base.configManager.configs.server.host, base.configManager.configs.server.port)
            .addListener { future ->
                if (future is ChannelFuture && future.isSuccess) {
                    serverChannel = future.channel()
                        .apply {
                            keepAliveManager.start()
                            logger.info("监听于 ${localAddress()}")

                            closeFuture().addListener {
                                keepAliveManager.stop()
                                logger.info("已关闭")
                            }
                        }
                } else {
                    logger.error("启动时发生异常", future.cause())
                }
            }
    }

    fun Channel.applyHandle() {
        val subNetworkManager = IRCNetworkManagerServer(this@NetworkManager)

        // Network | Proxy Protocol
        if (base.configManager.configs.server.proxyProtocol) {
            pipeline().addLast("proxy_protocol", HAProxyMessageDecoder())
        }

        pipeline().addLast("netty_address_logger", NettyAddressLogger(subNetworkManager))
        // 超时断开
        pipeline().addLast("timeout", ReadTimeoutHandler(IRCNetworkBaseConfig.READ_TIMEOUT_SECOND))
        // 入站包分片和解码
        pipeline().addLast("splitter", NettyVarInt21FrameDecoder())
        pipeline().addLast("decoder", NettyPacketDecoder(EnumPacketDirection.SERVER_BOUND))
        // 出站包分片和编码
        pipeline().addLast("prepender", NettyVarInt21FrameEncoder())
        pipeline().addLast("encoder", NettyPacketEncoder(EnumPacketDirection.CLIENT_BOUND))

        pipeline().addLast("base", subNetworkManager)
        pipeline().addLast("handler_hand_shake", HandlePacketHandShake(subNetworkManager))
        pipeline().addLast("handler_encryption_response", HandlePacketEncryptionResponse(subNetworkManager))
        pipeline().addLast("handler_login", HandlePacketLogin(subNetworkManager))
        pipeline().addLast("handler_online_keepalive", HandlePacketKeepAlive(subNetworkManager))
        pipeline().addLast("handler_online_message", HandlePacketMessage(subNetworkManager))
        pipeline().addLast("handler_online_input_status", HandlePacketInputStatus(subNetworkManager))
        pipeline().addLast("handler_online_session_state", HandlePacketSessionState(subNetworkManager))
        pipeline().addLast("handler_online_session_skin", HandlePacketSessionSkin(subNetworkManager))
        pipeline().addLast("handler_online_authentication", HandlePacketAuthentication(subNetworkManager))
    }

    fun stop() {
        clients.values.forEach { connection ->
            connection.kick(reason = "服务器被管理员关闭 请稍后重新连接", logout = false)
        }
        clients.clear()
        serverChannel.close()
    }
}