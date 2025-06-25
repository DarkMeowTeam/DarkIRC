package net.darkmeow.irc.network.handles.handshake

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.util.concurrent.Future
import io.netty.util.concurrent.GenericFutureListener
import net.darkmeow.irc.database.extensions.DataManagerClientExtensions.getClientMetadata
import net.darkmeow.irc.network.EnumConnectionState
import net.darkmeow.irc.network.IRCNetworkManagerServer
import net.darkmeow.irc.network.packet.handshake.c2s.C2SPacketEncryptionResponse
import net.darkmeow.irc.network.packet.handshake.s2c.S2CPacketHandShakeSuccess

class HandlePacketEncryptionResponse(private val connection: IRCNetworkManagerServer): SimpleChannelInboundHandler<C2SPacketEncryptionResponse>() {

    override fun channelRead0(ctx: ChannelHandlerContext, packet: C2SPacketEncryptionResponse) {
        runCatching {
            if (!connection.bossNetworkManager.base.configManager.configs.server.encryption) throw Exception("服务端未启用加密")

            connection.bossNetworkManager.base.dataManager.apply {
                if (connection.signatureData.isEmpty()) {
                    if (packet.hasSignatureResponse()) throw Exception("服务端未启用签名验证")
                } else {
                    if (!packet.hasSignatureResponse()) throw Exception("签名验证失败")

                    val meta = getClientMetadata(connection.brand.name)

                    if (!packet.verifySignature(connection.keyPair.public, meta.key.public, connection.signatureData)) {
                        throw Exception("签名验证失败")
                    }
                }
            }
        }
            .onFailure { e ->
                connection.kick(reason = e.message ?: "未知错误")
            }
            .onSuccess {
                connection.enableEncryption(packet.getSecretKey(connection.keyPair.private))
                connection.sendServerInfo()
                connection.sendPacket(S2CPacketHandShakeSuccess(connection.sessionId), GenericFutureListener<Future<Void>> { future ->
                    connection.connectionState = EnumConnectionState.LOGIN
                })
            }
    }
}