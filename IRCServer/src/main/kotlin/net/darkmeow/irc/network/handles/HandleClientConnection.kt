package net.darkmeow.irc.network.handles

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.handler.codec.haproxy.HAProxyMessage
import net.darkmeow.irc.network.NetworkManager
import net.darkmeow.irc.utils.ChannelAttrUtils.getUniqueId
import net.darkmeow.irc.utils.ChannelAttrUtils.setAddress
import net.darkmeow.irc.utils.ChannelAttrUtils.setUniqueId
import java.net.InetSocketAddress
import java.util.*

class HandleClientConnection(private val manager: NetworkManager): ChannelInboundHandlerAdapter() {

    override fun channelActive(ctx: ChannelHandlerContext) {
        ctx.channel().also { channel ->
            UUID.randomUUID().also { uuid ->
                channel.setUniqueId(uuid)

                manager.clients[uuid] = channel

                channel.setAddress(
                    (channel.remoteAddress() as? InetSocketAddress)
                        ?.let { "${it.address.hostAddress}:${it.port}" }
                        ?: "unknown"
                )
            }
        }
        super.channelActive(ctx)
    }

    override fun channelInactive(ctx: ChannelHandlerContext) {
        ctx
            .channel()
            .getUniqueId()
            .takeIf { it != UUID(0L, 0L) }
            ?.also {
                manager.clients.remove(it)
            }

        super.channelInactive(ctx)
    }

    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        if (msg is HAProxyMessage) {
            ctx.channel().setAddress("${msg.sourceAddress()}:${msg.sourcePort()}")
            msg.release()
        } else {
            super.channelRead(ctx, msg)
        }
    }
}