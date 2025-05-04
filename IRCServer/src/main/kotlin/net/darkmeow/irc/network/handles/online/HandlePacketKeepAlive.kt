package net.darkmeow.irc.network.handles.online

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import net.darkmeow.irc.network.IRCNetworkManagerServer
import net.darkmeow.irc.network.packet.online.c2s.C2SPacketKeepAlive

class HandlePacketKeepAlive(private val connection: IRCNetworkManagerServer): SimpleChannelInboundHandler<C2SPacketKeepAlive>() {

    override fun channelRead0(ctx: ChannelHandlerContext, packet: C2SPacketKeepAlive) {
        connection.updateLastKeepAlive()
    }
}