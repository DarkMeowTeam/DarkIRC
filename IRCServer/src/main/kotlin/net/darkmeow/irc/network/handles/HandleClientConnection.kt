package net.darkmeow.irc.network.handles

import io.netty.channel.ChannelHandlerAdapter
import io.netty.channel.ChannelHandlerContext
import net.darkmeow.irc.data.ClientData
import net.darkmeow.irc.data.datas.ConnectData
import net.darkmeow.irc.network.AttributeKeys
import net.darkmeow.irc.network.NetworkManager
import java.net.InetSocketAddress
import java.util.UUID

class HandleClientConnection(private val manager: NetworkManager): ChannelHandlerAdapter() {

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