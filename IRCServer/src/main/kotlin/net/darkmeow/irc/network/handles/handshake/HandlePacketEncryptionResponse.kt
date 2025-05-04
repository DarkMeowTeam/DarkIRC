package net.darkmeow.irc.network.handles.handshake

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.util.concurrent.Future
import io.netty.util.concurrent.GenericFutureListener
import net.darkmeow.irc.network.EnumConnectionState
import net.darkmeow.irc.network.IRCNetworkManagerServer
import net.darkmeow.irc.network.packet.handshake.c2s.C2SPacketEncryptionResponse
import net.darkmeow.irc.network.packet.handshake.s2c.S2CPacketDenyHandShake
import net.darkmeow.irc.network.packet.handshake.s2c.S2CPacketHandShakeSuccess

class HandlePacketEncryptionResponse(private val connection: IRCNetworkManagerServer): SimpleChannelInboundHandler<C2SPacketEncryptionResponse>() {

    override fun channelRead0(ctx: ChannelHandlerContext, packet: C2SPacketEncryptionResponse) {
        if (!connection.bossNetworkManager.base.configManager.configs.ircServer.encryption) {
            connection.sendPacket(S2CPacketDenyHandShake("服务端未启用加密"))
            return
        }

        connection.enableEncryption(packet.getSecretKey(connection.keyPair.private))
        connection.sendPacket(S2CPacketHandShakeSuccess(connection.sessionId), GenericFutureListener<Future<Void>> { future ->
            connection.connectionState = EnumConnectionState.LOGIN
        })
    }
}