package net.darkmeow.irc.network.handles.handshake

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.util.concurrent.Future
import io.netty.util.concurrent.GenericFutureListener
import net.darkmeow.irc.database.extensions.DataManagerClientExtensions.clientExist
import net.darkmeow.irc.database.extensions.DataManagerClientExtensions.getClientMetadata
import net.darkmeow.irc.network.EnumConnectionState
import net.darkmeow.irc.network.IRCNetworkManagerServer
import net.darkmeow.irc.network.packet.handshake.c2s.C2SPacketHandShake
import net.darkmeow.irc.network.packet.handshake.s2c.S2CPacketEnableCompression
import net.darkmeow.irc.network.packet.handshake.s2c.S2CPacketEncryptionRequest
import net.darkmeow.irc.network.packet.handshake.s2c.S2CPacketHandShakeSuccess
import net.darkmeow.irc.utils.CryptUtils
import java.util.*

class HandlePacketHandShake(private val connection: IRCNetworkManagerServer): SimpleChannelInboundHandler<C2SPacketHandShake>() {

    override fun channelRead0(ctx: ChannelHandlerContext, packet: C2SPacketHandShake) {
        runCatching {
            connection.bossNetworkManager.base.dataManager.apply {
                if (!clientExist(packet.brand.name)) throw Exception("您的客户端已停用")

                val meta = getClientMetadata(packet.brand.name)

                if (packet.brand.key != meta.key) throw Exception("您的客户端版本已过时")
                if (packet.brand.versionId < meta.metadata.allowLoginMinVersion) throw Exception("您的客户端版本已过时")
            }
            
            if (packet.hardWareUniqueId.isEmpty()) throw Exception("服务器正在维护中")
        }
            .onSuccess {
                val config = connection.bossNetworkManager.base.configManager.configs.ircServer
                val id = UUID.randomUUID()

                connection.protocolVersion = packet.protocolVersion
                connection.sessionId = id
                connection.brand = packet.brand
                connection.hardWareUniqueId = packet.hardWareUniqueId
                connection.updateLastKeepAlive()

                if (config.compression.state) {
                    connection.sendPacket(S2CPacketEnableCompression(config.compression.threshold), GenericFutureListener<Future<Void>> { future ->
                        connection.enableCompression(config.compression.threshold)
                    })
                }

                if (config.encryption) {
                    // 加密会话 & 签名验证 流程
                    connection.keyPair = CryptUtils.generateKeyPair()
                    if (config.signature) {
                        connection.signatureCode = "random"
                        connection.sendPacket(S2CPacketEncryptionRequest(connection.keyPair.public, connection.signatureCode))
                    } else {
                        connection.signatureCode = ""
                        connection.sendPacket(S2CPacketEncryptionRequest(connection.keyPair.public))
                    }
                } else {
                    // 直接握手成功
                    connection.sendPacket(S2CPacketHandShakeSuccess(id), GenericFutureListener<Future<Void>> { future ->
                        connection.connectionState = EnumConnectionState.LOGIN
                    })
                }
            }
            .onFailure { e ->
                connection.kick(reason = e.message ?: "未知错误")
            }
    }
}