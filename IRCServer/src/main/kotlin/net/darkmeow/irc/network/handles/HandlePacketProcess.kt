package net.darkmeow.irc.network.handles

import com.google.gson.JsonParser
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import net.darkmeow.irc.IRCLib
import net.darkmeow.irc.data.DataSessionInfo
import net.darkmeow.irc.data.UserInfoData
import net.darkmeow.irc.network.AttributeKeys
import net.darkmeow.irc.network.NetworkManager
import net.darkmeow.irc.network.PacketUtils
import net.darkmeow.irc.network.packet.c2s.*
import net.darkmeow.irc.network.packet.s2c.*
import net.darkmeow.irc.network.packet.s2c.S2CPacketLoginResult.LoginResult
import net.darkmeow.irc.utils.ChannelAttrUtils.getCurrentUser
import net.darkmeow.irc.utils.ChannelAttrUtils.getDevice
import net.darkmeow.irc.utils.ChannelAttrUtils.getSessionInfo
import net.darkmeow.irc.utils.ChannelAttrUtils.getSessionOptions
import net.darkmeow.irc.utils.ChannelAttrUtils.getUniqueId
import net.darkmeow.irc.utils.ChannelAttrUtils.kick
import net.darkmeow.irc.utils.ChannelAttrUtils.setCurrentUser
import net.darkmeow.irc.utils.ChannelAttrUtils.setDevice
import net.darkmeow.irc.utils.ChannelAttrUtils.setLatestKeepAlive
import net.darkmeow.irc.utils.ChannelAttrUtils.setProtocolVersion
import net.darkmeow.irc.utils.ChannelAttrUtils.setSessionInfo
import net.darkmeow.irc.utils.ChannelAttrUtils.setSessionOptions
import net.darkmeow.irc.utils.ChannelUtils.sendPacket
import net.darkmeow.irc.utils.ChannelUtils.sendSystemMessage
import java.util.UUID

class HandlePacketProcess(private val manager: NetworkManager): ChannelInboundHandlerAdapter() {

    override fun channelActive(ctx: ChannelHandlerContext) {
        ctx.channel().apply {
            attr(AttributeKeys.LATEST_KEEPALIVE).set(System.currentTimeMillis())
        }

        super.channelActive(ctx)
    }

    override fun channelInactive(ctx: ChannelHandlerContext) {
        ctx.channel()
            .takeIf { it.hasAttr(AttributeKeys.CURRENT_USER) }
            ?.attr(AttributeKeys.CURRENT_USER)
            ?.get()
            ?.also { name ->
                manager.logger.info("[-] $name")

                manager.clients.values
                    .filter { otherChannel -> otherChannel.hasAttr(AttributeKeys.CURRENT_USER) }
                    .onEach { otherChannel ->
                        run updateUser@ {
                             // 告诉客户端这个 id 已经离线了
                            otherChannel.sendPacket(
                                S2CPacketUpdateOtherSessionInfo(
                                    ctx.channel().getUniqueId(),
                                    null
                                )
                            )
                        }
                    }
            }

        super.channelInactive(ctx)
    }

    override fun channelRead(ctx: ChannelHandlerContext, data: Any) {
         JsonParser.parseString(data as? String)?.asJsonObject?.also { obj ->
            PacketUtils.resolveClientPacket(obj)?.also packetHandle@ { packet ->
                val channel = ctx.channel()

                when (packet) {
                    is C2SPacketHandShake -> {
                        channel.setProtocolVersion(packet.protocolVersion)
                        channel.setDevice(packet.deviceId)
                        channel.sendPacket(S2CPacketHandShake(IRCLib.PROTOCOL_VERSION))
                    }
                    is C2SPacketKeepAlive -> channel.setLatestKeepAlive(System.currentTimeMillis())
                    is C2SPacketLogin -> run {
                        class ExceptionLoginResult(val result: LoginResult): Exception()

                        runCatching {
                            if (!channel.hasAttr(AttributeKeys.DEVICE)) {
                                throw ExceptionLoginResult(LoginResult.OUTDATED_CLIENT_VERSION)
                            }

                            manager.base.dataManager
                                .getClientMinLoginVersion(packet.client.id, packet.client.hash)
                                ?.also {
                                    if (it > packet.client.versionId) throw ExceptionLoginResult(LoginResult.OUTDATED_CLIENT_VERSION)
                                }
                                ?: throw ExceptionLoginResult(LoginResult.INVALID_CLIENT)

                            manager.base.dataManager
                                .checkUserPassword(packet.name, packet.password)
                                .takeIf { !it }
                                ?.also {
                                    throw ExceptionLoginResult(LoginResult.USER_OR_PASSWORD_WRONG)
                                }

                            if (!packet.notOnline) {
                                // 登录成功上报 (但是不设置信息 因为等会需要失效其它设备)
                                channel.sendPacket(
                                    S2CPacketUpdateMySessionInfo(
                                        packet.name,
                                        manager.base.dataManager.getUserRank(packet.name) ?: "",
                                        manager.base.dataManager.getUserPremium(packet.name),
                                        channel.getUniqueId()
                                    )
                                )

                                // 登出其他客户端
                                manager.clients
                                    .takeIf { !manager.base.configManager.configs.userLimit.allowMultiDeviceLogin }
                                    ?.filter { (_, otherChannel) ->
                                        otherChannel.getCurrentUser() == packet.name
                                    }
                                    ?.filter { (_, otherChannel) ->
                                        otherChannel.getDevice() != channel.getDevice()
                                    }
                                    ?.onEach { (_, otherChannel) ->
                                        otherChannel.kick(
                                            reason = "账号在另一设备登录",
                                            logout = false
                                        )
                                    }

                                // 登录成功
                                channel.setSessionInfo(DataSessionInfo(packet.client))
                                channel.setCurrentUser(packet.name)
                            }

                            manager.logger.info("[+] ${packet.name}  (${channel.attr(AttributeKeys.ADDRESS).get()} ${channel.attr(AttributeKeys.DEVICE).get()})${if(packet.notOnline) " (仅验证密码)" else ""}")

                            throw ExceptionLoginResult(LoginResult.SUCCESS)
                        }
                            .onFailure { t ->
                                when (t) {
                                    is ExceptionLoginResult -> channel.sendPacket(
                                        S2CPacketLoginResult(t.result)
                                    )
                                    else -> channel.sendPacket(
                                        S2CPacketMessageSystem("系统错误: ${t.message}")
                                    )
                                }
                            }
                    }
                    is C2SPacketChatPublic -> {
                        val user = channel.getCurrentUser() ?: return@packetHandle

                        val boardCastPacket = S2CPacketMessagePublic(
                            channel.getUniqueId(),
                            UserInfoData(
                                user,
                                manager.base.dataManager.getUserRank(user) ?: "",
                                channel.getSessionInfo(),
                                channel.getSessionOptions()
                            ),
                            packet.message
                                .replace("&", "§")
                                .replace("\n", "")
                                .replace("\r", "")
                        )

                        manager.logger.info("[${user}] ${boardCastPacket.message}")

                        manager.clients
                            .values
                            .filter { otherChannel -> otherChannel.hasAttr(AttributeKeys.CURRENT_USER) }
                            .onEach { otherChannel ->
                                // 同时也会发送给发送者客户端上 这不是bug 而是刻意这么设计的
                                otherChannel.sendPacket(boardCastPacket)
                            }
                    }
                    is C2SPacketChatPrivate -> {
                        val user = channel.getCurrentUser() ?: return@packetHandle

                        manager.clients.values
                            .filter { otherChannel -> otherChannel.getCurrentUser() == packet.user || otherChannel.getUniqueId().toString() == packet.user }
                            .also {
                                // 接收方不在线
                                if (it.isEmpty()) {
                                    channel.sendPacket(S2CPacketMessagePrivateResult(packet.user, packet.message, false))
                                    return@packetHandle
                                }
                            }
                            .also {
                                val boardCastPacket = S2CPacketMessagePrivate(
                                    channel.getUniqueId(),
                                    UserInfoData(
                                        user,
                                        manager.base.dataManager.getUserRank(user) ?: "",
                                        channel.getSessionInfo(),
                                        channel.getSessionOptions()
                                    ),
                                    packet.message
                                        .replace("&", "§")
                                        .replace("\n", "")
                                        .replace("\r", "")
                                )

                                channel.sendPacket(S2CPacketMessagePrivateResult(packet.user, packet.message, true))
                                // 兼容多客户端登录 (适用于 同一设备多开客户端)
                                it.onEach { otherChannel ->
                                    otherChannel.sendPacket(boardCastPacket)
                                }
                            }
                    }
                    is C2SPacketCommand -> {
                        val user = channel.getCurrentUser() ?: return@packetHandle

                        manager.logger.info("[${user}] 使用指令: ${packet.root} ${packet.args.joinToString(" ")}")
                        manager.base.commandManager.handle(channel, packet.root, packet.args.toMutableList())
                    }
                    is C2SPacketUpdateSessionOptions -> {
                        val user = channel.getCurrentUser() ?: return@packetHandle

                        if (runCatching { channel.getSessionOptions() }.getOrNull()?.let { packet.options.session.name != it.session.name || packet.options.server != it.server } == true) {
                            manager.logger.info("[${user}] 游戏状态更新: ${packet.options.session.name} ${packet.options.server}")
                        }

                        channel.setSessionOptions(packet.options)

                        val boardCastPacket =
                            S2CPacketUpdateOtherSessionInfo(
                                channel.getUniqueId(),
                                UserInfoData(
                                    user,
                                    manager.base.dataManager.getUserRank(user) ?: return@packetHandle,
                                    channel.getSessionInfo(),
                                    packet.options
                                )
                            )
                        manager.clients.values
                            .filter { otherChannel -> otherChannel.hasAttr(AttributeKeys.CURRENT_USER) }
                            .onEach { otherChannel ->
                                otherChannel.sendPacket(boardCastPacket)
                            }
                    }
                    is C2SPacketQueryUsers -> {
                        if (!channel.hasAttr(AttributeKeys.CURRENT_USER)) return@packetHandle

                        val users = HashMap<UUID, UserInfoData>()

                        manager.clients.values
                            .filter { otherChannel -> otherChannel.hasAttr(AttributeKeys.CURRENT_USER) }
                            .filter { otherChannel ->
                                if (packet.onlySameServer) {
                                    otherChannel.getSessionOptions().server == channel.getSessionOptions().server
                                } else {
                                    true
                                }
                            }
                            .onEach { otherChannel ->
                                run queryUser@ {
                                    val name = otherChannel.getCurrentUser() ?: return@queryUser

                                    users[otherChannel.getUniqueId()] = UserInfoData(
                                        name,
                                        manager.base.dataManager.getUserRank(name) ?: return@queryUser,
                                        otherChannel.getSessionInfo(),
                                        otherChannel.getSessionOptions()
                                    )
                                }
                            }
                            .also {
                                // 告诉客户端这个 id 是 irc 内用户
                                channel.sendPacket(
                                    S2CPacketUpdateMultiSessionInfo(
                                        packet.onlySameServer,
                                        true,
                                        users
                                    )
                                )
                            }
                    }
                    is C2SPacketChangePassword -> {
                        val user = channel.getCurrentUser() ?: return@packetHandle

                        manager.base.dataManager.setUserPassword(user, packet.password)

                        manager.base.networkManager.clients.values
                            .filter { otherChannel -> otherChannel.getCurrentUser() == user }
                            .onEach { otherChannel ->
                                otherChannel.kick(
                                    reason = "当前账号密码已修改,请重新登录",
                                    logout = true
                                )
                            }
                    }
                    else -> { }
                }
            }
        }
    }
}