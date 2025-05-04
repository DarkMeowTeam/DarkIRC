package net.darkmeow.irc.network.handles.online

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import net.darkmeow.irc.data.DataSkin
import net.darkmeow.irc.network.IRCNetworkManagerServer
import net.darkmeow.irc.network.packet.C2SPacket
import net.darkmeow.irc.network.packet.online.c2s.C2SPacketQuerySkin
import net.darkmeow.irc.network.packet.online.c2s.C2SPacketUploadSkin
import net.darkmeow.irc.network.packet.online.s2c.S2CPacketUpdateSkin
import net.darkmeow.irc.utils.QuickBoardCastUtils.sendPacketToAllIgnoreInvisible

class HandlePacketSessionSkin(private val connection: IRCNetworkManagerServer): SimpleChannelInboundHandler<C2SPacket>() {

    override fun channelRead0(ctx: ChannelHandlerContext, packet: C2SPacket) {
        when(packet) {
            is C2SPacketUploadSkin -> {
                connection.sessionSkin = packet.skin
                connection.sendPacketToAllIgnoreInvisible(S2CPacketUpdateSkin(connection.sessionId))
            }
            is C2SPacketQuerySkin -> {
                connection.bossNetworkManager.clients[packet.sessionId]
                    ?.takeUnless { client -> client.currentIsInvisible }
                    ?.also { client ->
                        connection.sendPacket(S2CPacketUpdateSkin(packet.sessionId, client.sessionSkin ?: DataSkin.EMPTY))
                    }
            }
            else -> ctx.fireChannelRead(packet)
        }
    }

    override fun channelInactive(ctx: ChannelHandlerContext) {
        connection.sendPacketToAllIgnoreInvisible(S2CPacketUpdateSkin(connection.sessionId))

        super.channelInactive(ctx)
    }
}