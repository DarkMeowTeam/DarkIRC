package net.darkmeow.irc.network.handles

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.handler.codec.haproxy.HAProxyMessage
import net.darkmeow.irc.network.AttributeKeys
import net.darkmeow.irc.network.NetworkManager
import java.net.InetSocketAddress
import java.util.*

class HandleClientConnection(private val manager: NetworkManager): ChannelInboundHandlerAdapter() {

    override fun channelActive(ctx: ChannelHandlerContext) {
        ctx.channel().also { channel ->
            UUID.randomUUID().also { uuid ->
                channel.attr(AttributeKeys.UUID).set(uuid)

                manager.clients[uuid] = channel

                channel.attr(AttributeKeys.ADDRESS).set(
                    (channel.remoteAddress() as? InetSocketAddress)
                        ?.let { "${it.address.hostAddress}:${it.port}" }
                        ?: "null"
                )
            }
        }
        super.channelActive(ctx)
    }

    override fun channelInactive(ctx: ChannelHandlerContext) {
        ctx
            .channel()
            .takeIf { it.hasAttr(AttributeKeys.UUID) }
            ?.attr(AttributeKeys.UUID)?.get()
            ?.also {
                manager.clients.remove(it)
            }

        super.channelInactive(ctx)
    }

    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        if (msg is HAProxyMessage) {
            ctx.channel().attr(AttributeKeys.ADDRESS).set(
                "${msg.sourceAddress()}:${msg.sourcePort()}"
            )
            msg.release()
        } else {
            super.channelRead(ctx, msg)
        }
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        runCatching {
            channelInactive(ctx)
        }
    }
}