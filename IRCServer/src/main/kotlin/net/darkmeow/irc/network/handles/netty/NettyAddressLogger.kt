package net.darkmeow.irc.network.handles.netty

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.handler.codec.haproxy.HAProxyMessage
import net.darkmeow.irc.network.IRCNetworkManagerServer
import java.net.InetSocketAddress

class NettyAddressLogger(private val connection: IRCNetworkManagerServer): ChannelInboundHandlerAdapter() {

    override fun channelActive(ctx: ChannelHandlerContext) {
        connection.address = ctx.channel().remoteAddress().toString()

        super.channelActive(ctx)
    }

    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        if (msg is HAProxyMessage) {
            connection.address = "${msg.sourceAddress()}:${msg.sourcePort()}"
            msg.release()
        } else {
            super.channelRead(ctx, msg)
        }
    }
}