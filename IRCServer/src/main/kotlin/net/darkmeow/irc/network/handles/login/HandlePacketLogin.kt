package net.darkmeow.irc.network.handles.login

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.util.concurrent.Future
import io.netty.util.concurrent.GenericFutureListener
import net.darkmeow.irc.data.enmus.EnumUserPremium
import net.darkmeow.irc.network.EnumConnectionState
import net.darkmeow.irc.network.IRCNetworkManagerServer
import net.darkmeow.irc.network.NetworkManager
import net.darkmeow.irc.network.packet.login.c2s.C2SPacketLogin
import net.darkmeow.irc.network.packet.login.s2c.S2CPacketLoginFailed
import net.darkmeow.irc.network.packet.login.s2c.S2CPacketLoginSuccess
import net.darkmeow.irc.network.packet.online.s2c.S2CPacketUpdateMyProfile

class HandlePacketLogin(private val manager: NetworkManager, private val connection: IRCNetworkManagerServer): SimpleChannelInboundHandler<C2SPacketLogin>() {

    override fun channelRead0(ctx: ChannelHandlerContext, packet: C2SPacketLogin) {
        val isTokenLogin = packet.password.length == 128

        runCatching {
            when (isTokenLogin) {
                // session token
                true -> manager.base.dataManager
                    .let {
                        arrayOf(
                            it.getSessionLinkUser(packet.password) != packet.username,
                            !it.userExist(packet.username)
                        ).any { flag -> flag }
                    }
                    .takeIf { it }
                    ?.also {
                        if (manager.base.dataManager.sessionExist(packet.password)) {
                            manager.base.dataManager.deleteSession(packet.password)
                        }

                        throw Exception("登录信息失效, 请重新登录")
                    }
                // password
                false -> manager.base.dataManager
                    .checkUserPassword(packet.username, packet.password)
                    .takeUnless { it }
                    ?.also {
                        throw Exception("用户名或密码错误")
                    }
            }

            // 客户端权限检查
            manager.base.dataManager
                // 忽略 IRC 管理员
                .takeIf { it.getUserPremium(packet.username).ordinal < EnumUserPremium.ADMIN.ordinal }
                // 客户端管理员/客户端用户
                ?.takeIf { it.getClientUsers(connection.brand.name)?.contains(packet.username) != true }
                ?.takeIf { it.getClientAdministrators(connection.brand.name)?.contains(packet.username) != true }
                ?.also { throw Exception("无登录此客户端权限") }
        }
            .onSuccess {
                var uploadToken: String? = packet.password

                if (isTokenLogin) {
                    manager.base.dataManager.updateSessionInfo(
                        packet.password,
                        System.currentTimeMillis(),
                        connection.hardWareUniqueId,
                        connection.address
                    )


                } else if (!packet.isDisableGenerateToken) {
                    @Suppress("SpellCheckingInspection")
                    uploadToken = (1..128)
                        .map { "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".random() }
                        .joinToString("")

                    manager.base.dataManager.createSession(
                        uploadToken,
                        packet.username,
                        System.currentTimeMillis(),
                        connection.hardWareUniqueId,
                        connection.address
                    )
                } else {
                    uploadToken = null
                }

                val premium = manager.base.dataManager.getUserPremium(user = packet.username)

                connection.currentToken = uploadToken
                connection.sendPacket(S2CPacketLoginSuccess(packet.username, uploadToken ?: ""), GenericFutureListener<Future<Void>> { future ->
                    connection.connectionState = EnumConnectionState.ONLINE
                    connection.sendPacket(S2CPacketUpdateMyProfile(packet.username, premium, packet.isInvisible))
                })

                // 登出其他客户端
                manager.clients
                    .takeIf { !manager.base.configManager.configs.userLimit.allowMultiDeviceLogin }
                    ?.filter { (_, other) ->
                        other.user == packet.username
                    }
                    ?.filter { (_, other) ->
                        other.hardWareUniqueId != connection.hardWareUniqueId
                    }
                    ?.onEach { (_, other) ->
                        other.disconnect(
                            reason = "账号在另一设备登录",
                            logout = false
                        )
                    }

                // 登录成功
                connection.user = packet.username
                connection.userPremium = premium

                manager.clients[connection.sessionId] = connection

                manager.logger.info(
                    StringBuilder()
                        .append("[+] ${packet.username}")
                        .append("  ")
                        .append("(${connection.address} ${connection.hardWareUniqueId})")
                        .append(if(packet.isDisableGenerateToken) " (单次登录)" else "")
                        .append(if(connection.currentIsInvisible) " (隐身登录)" else "")
                        .toString()
                )
            }
            .onFailure { e ->
                connection.sendPacket(S2CPacketLoginFailed(e.message ?: "未知错误"))
            }
    }
}