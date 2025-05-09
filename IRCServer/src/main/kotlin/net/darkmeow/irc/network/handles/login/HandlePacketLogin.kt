package net.darkmeow.irc.network.handles.login

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.util.concurrent.Future
import io.netty.util.concurrent.GenericFutureListener
import net.darkmeow.irc.data.base.DataSession
import net.darkmeow.irc.data.enmus.EnumUserPremium
import net.darkmeow.irc.database.extensions.DataManagerClientExtensions.getClientMetadata
import net.darkmeow.irc.database.extensions.DataManagerSessionExtensions.createSession
import net.darkmeow.irc.database.extensions.DataManagerSessionExtensions.deleteSession
import net.darkmeow.irc.database.extensions.DataManagerSessionExtensions.getSessionMetadata
import net.darkmeow.irc.database.extensions.DataManagerSessionExtensions.sessionExist
import net.darkmeow.irc.database.extensions.DataManagerSessionExtensions.updateSession
import net.darkmeow.irc.database.extensions.DataManagerUserExtensions.getUserMetadata
import net.darkmeow.irc.database.extensions.DataManagerUserExtensions.userExist
import net.darkmeow.irc.network.EnumConnectionState
import net.darkmeow.irc.network.IRCNetworkManagerServer
import net.darkmeow.irc.network.NetworkManager
import net.darkmeow.irc.network.packet.login.c2s.C2SPacketLogin
import net.darkmeow.irc.network.packet.login.s2c.S2CPacketLoginSuccess
import net.darkmeow.irc.network.packet.online.s2c.S2CPacketUpdateMyProfile

class HandlePacketLogin(private val manager: NetworkManager, private val connection: IRCNetworkManagerServer): SimpleChannelInboundHandler<C2SPacketLogin>() {

    class AuthenticationException(val msg: String, val markSessionTokenInvalid: Boolean) : Exception()

    override fun channelRead0(ctx: ChannelHandlerContext, packet: C2SPacketLogin) {
        val isTokenLogin = packet.password.length == 128

        runCatching {
            manager.base.dataManager.apply {
                run passwordVerify@ {
                    if (isTokenLogin) {
                        // token 不存在
                        if (!sessionExist(packet.password)) throw AuthenticationException("登录信息失效, 请重新登录", true)
                        val meta = getSessionMetadata(packet.password)
                        // token 对应的 用户 错误
                        if (meta.metadata.user != packet.username) throw AuthenticationException("登录信息失效, 请重新登录", true)
                        // token 对应的 用户 不存在(已被删除)
                        if (!userExist(meta.metadata.user)) {
                            deleteSession(meta.metadata.user)
                            throw AuthenticationException("登录信息失效, 请重新登录", true)
                        }
                    } else {
                        // 用户名不存在
                        if (!userExist(packet.username)) throw AuthenticationException("用户名或密码错误", true)
                        val meta = getUserMetadata(packet.username)
                        // 密码错误
                        if (meta.metadata.password != packet.password) throw AuthenticationException("用户名或密码错误", true)
                        // 用户已被封禁
                        if (meta.metadata.premium == EnumUserPremium.BANNED) throw AuthenticationException("用户已被封禁, 无法登录", true)
                    }
                }
                run clientVerify@ {
                    val userMeta = getUserMetadata(packet.username)
                    val clientMeta = getClientMetadata(connection.brand.name)

                    // 管理员用户无需客户端权限检查
                    if (userMeta.metadata.premium.ordinal >= EnumUserPremium.ADMIN.ordinal) return@clientVerify
                    // 客户端管理员/客户端用户
                    if (clientMeta.metadata.clientUsers.contains(userMeta.name)) return@clientVerify
                    if (clientMeta.metadata.clientAdministrators.contains(userMeta.name)) return@clientVerify

                    throw AuthenticationException("无登录此客户端权限", false)
                }
            }
        }
            .onSuccess {
                val userMeta = manager.base.dataManager.getUserMetadata(packet.username)
                var uploadToken = ""

                if (isTokenLogin) {
                    uploadToken = packet.password
                    manager.base.dataManager.updateSession(
                        token = packet.password,
                        metadata = DataSession.SessionMetadata(
                            user = packet.username,
                            lastLoginTimestamp = System.currentTimeMillis(),
                            lastLoginHardWareUniqueId = connection.hardWareUniqueId,
                            lastLoginIp = connection.address
                        )
                    )
                } else if (!packet.isDisableGenerateToken) {
                    uploadToken = manager.base.dataManager.createSession(
                        metadata = DataSession.SessionMetadata(
                            user = packet.username,
                            lastLoginTimestamp = System.currentTimeMillis(),
                            lastLoginHardWareUniqueId = connection.hardWareUniqueId,
                            lastLoginIp = connection.address
                        )
                    ).token
                }

                connection.sendPacket(S2CPacketLoginSuccess(packet.username, uploadToken), GenericFutureListener<Future<Void>> { future ->
                    connection.currentToken = uploadToken.takeIf { it.isNotEmpty() }

                    connection.connectionState = EnumConnectionState.ONLINE
                    connection.sendPacket(S2CPacketUpdateMyProfile(packet.username, userMeta.metadata.premium, packet.isInvisible))
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
                        other.kick(
                            reason = "账号在另一设备登录",
                            logout = false
                        )
                    }

                // 登录成功
                connection.user = userMeta.name
                connection.userPremium = userMeta.metadata.premium

                manager.clients[connection.sessionId] = connection

                manager.logger.info(
                    StringBuilder()
                        .append("[+] ${userMeta.name}")
                        .append("  ")
                        .append("(${connection.address} ${connection.hardWareUniqueId})")
                        .append(if(packet.isDisableGenerateToken) " (单次登录)" else "")
                        .append(if(connection.currentIsInvisible) " (隐身登录)" else "")
                        .toString()
                )
            }
            .onFailure { e ->
                when (e) {
                    is AuthenticationException -> connection.kick(e.msg, e.markSessionTokenInvalid)
                    else -> connection.kick("服务端异常 (${e.cause?.javaClass?.simpleName}: ${e.cause?.message})")
                }
            }
    }
}