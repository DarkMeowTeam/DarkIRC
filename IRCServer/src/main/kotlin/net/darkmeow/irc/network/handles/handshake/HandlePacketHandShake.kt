package net.darkmeow.irc.network.handles.handshake

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.util.concurrent.Future
import io.netty.util.concurrent.GenericFutureListener
import net.darkmeow.irc.network.EnumConnectionState
import net.darkmeow.irc.network.IRCNetworkManagerServer
import net.darkmeow.irc.network.NetworkManager
import net.darkmeow.irc.network.packet.handshake.c2s.C2SPacketHandShake
import net.darkmeow.irc.network.packet.handshake.s2c.S2CPacketDenyHandShake
import net.darkmeow.irc.network.packet.handshake.s2c.S2CPacketHandShakeSuccess
import java.util.*

class HandlePacketHandShake(private val manager: NetworkManager, private val connection: IRCNetworkManagerServer): SimpleChannelInboundHandler<C2SPacketHandShake>() {

    override fun channelRead0(ctx: ChannelHandlerContext, packet: C2SPacketHandShake) {
        runCatching {
            manager.base.dataManager
                .getClientMinLoginVersion(packet.brand.name, packet.brand.key)
                ?.also { if (it > packet.brand.versionId) throw Exception("您的客户端版本已过时") }
                ?: throw Exception("您的客户端已停用")

            if (packet.hardWareUniqueId.isEmpty()) throw Exception("服务器正在维护中")
        }
            .onSuccess {
                val id = UUID.randomUUID()

                connection.protocolVersion = packet.protocolVersion
                connection.sessionId = id
                connection.brand = packet.brand
                connection.hardWareUniqueId = packet.hardWareUniqueId
                connection.updateLastKeepAlive()

                connection.sendPacket(S2CPacketHandShakeSuccess(id), GenericFutureListener<Future<Void>> { future ->
                    connection.connectionState = EnumConnectionState.LOGIN
                })
            }
            .onFailure { e ->
                connection.sendPacket(S2CPacketDenyHandShake(e.message ?: "未知错误"))
            }
    }
}