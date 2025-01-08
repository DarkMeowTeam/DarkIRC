package net.darkmeow.irc.network.handles

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import net.darkmeow.irc.network.AttributeKeys
import net.darkmeow.irc.network.NetworkManager
import java.util.*

class HandleClientConnection(private val manager: NetworkManager): ChannelInboundHandlerAdapter() {

    override fun channelActive(ctx: ChannelHandlerContext) {
        val channel = ctx.channel()

        UUID.randomUUID().also { uuid ->
            ctx.attr(AttributeKeys.UUID).set(uuid)

            manager.clients[uuid] = channel
        }

        super.channelActive(ctx)
    }

    override fun channelInactive(ctx: ChannelHandlerContext) {
        ctx
            .takeIf { it.hasAttr(AttributeKeys.UUID) }
            ?.attr(AttributeKeys.UUID)?.get()
            ?.also {
                manager.clients.remove(it)
            }

        super.channelInactive(ctx)
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        runCatching {
            channelInactive(ctx)
        }
    }
}