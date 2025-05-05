package net.darkmeow.irc.network.handles.handshake

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.util.concurrent.Future
import io.netty.util.concurrent.GenericFutureListener
import net.darkmeow.irc.network.EnumConnectionState
import net.darkmeow.irc.network.IRCNetworkManagerServer
import net.darkmeow.irc.network.packet.handshake.c2s.C2SPacketSignatureResponse
import net.darkmeow.irc.network.packet.handshake.s2c.S2CPacketEncryptionRequest
import net.darkmeow.irc.network.packet.handshake.s2c.S2CPacketHandShakeSuccess
import net.darkmeow.irc.utils.CryptUtils

class HandlePacketSignatureResponse(private val connection: IRCNetworkManagerServer): SimpleChannelInboundHandler<C2SPacketSignatureResponse>() {

    override fun channelRead0(ctx: ChannelHandlerContext, packet: C2SPacketSignatureResponse) {
        if (!connection.bossNetworkManager.base.configManager.configs.ircServer.signature) {
            connection.kick(reason = "服务端未启用签名认证")
            return
        }
        if (connection.signatureCode != packet.code || !packet.verify(connection.bossNetworkManager.base.configManager.signatureKey)) {
            connection.kick(reason = "签名验证失败")
            return
        }

        if (connection.bossNetworkManager.base.configManager.configs.ircServer.encryption) {
            // 要求加密会话
            connection.keyPair = CryptUtils.generateKeyPair()
            connection.sendPacket(S2CPacketEncryptionRequest(connection.keyPair.public))
        } else {
            // 直接握手成功
            connection.sendPacket(S2CPacketHandShakeSuccess(connection.sessionId), GenericFutureListener<Future<Void>> { future ->
                connection.connectionState = EnumConnectionState.LOGIN
            })
        }
    }
}