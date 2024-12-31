package net.darkmeow.irc.network.handles

import com.google.gson.JsonParser
import io.netty.channel.ChannelHandlerAdapter
import io.netty.channel.ChannelHandlerContext
import net.darkmeow.irc.IRCLib
import net.darkmeow.irc.data.GameInfoData
import net.darkmeow.irc.data.UserInfoData
import net.darkmeow.irc.network.AttributeKeys
import net.darkmeow.irc.network.NetworkManager
import net.darkmeow.irc.network.PacketUtils
import net.darkmeow.irc.network.packet.c2s.*
import net.darkmeow.irc.network.packet.s2c.*
import net.darkmeow.irc.network.packet.s2c.S2CPacketLoginResult.LoginResult
import net.darkmeow.irc.utils.ChannelUtils.sendPacket
import java.net.InetSocketAddress

class HandlePacketProcess(private val manager: NetworkManager): ChannelHandlerAdapter() {

    override fun channelActive(ctx: ChannelHandlerContext) {
        ctx.attr(AttributeKeys.LATEST_KEEPALIVE).set(System.currentTimeMillis())
        ctx.attr(AttributeKeys.GAME_INFO).set(GameInfoData.EMPTY)

        super.channelActive(ctx)
    }

    override fun channelInactive(ctx: ChannelHandlerContext) {
        ctx
            .takeIf { it.hasAttr(AttributeKeys.CURRENT_USER) }
            ?.attr(AttributeKeys.CURRENT_USER)?.get()
            ?.also { name ->
                manager.logger.info("[-] $name")

                val rank = manager.base.dataManager.getUserRank(name) ?: return@also

                manager.clients.values
                    .filter { channel -> channel.hasAttr(AttributeKeys.CURRENT_USER) }
                    .onEach { channel ->
                        run updateUser@ {
                             // 告诉客户端这个 id 已经离线了
                            channel.sendPacket(
                                S2CPacketUpdateOtherInfo(
                                    name,
                                    UserInfoData(
                                        false,
                                        rank,
                                        null
                                    )
                                )
                            )
                        }
                    }
            }

        super.channelInactive(ctx)
    }

    override fun channelRead(ctx: ChannelHandlerContext, data: Any) {
        JsonParser.parseString(data as String).asJsonObject.also { obj ->
            PacketUtils.resolveClientPacket(obj).also packetHandle@ { packet ->
                when (packet) {
                    is C2SPacketHandShake -> ctx.sendPacket(S2CPacketHandShake(IRCLib.PROTOCOL_VERSION))
                    is C2SPacketKeepAlive -> ctx.attr(AttributeKeys.LATEST_KEEPALIVE).set(System.currentTimeMillis())
                    is C2SPacketLogin -> run {
                        class ExceptionLoginResult(val result: LoginResult): Exception()

                        runCatching {
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

                            // 登录成功上报 (但是不设置信息 因为等会需要失效其它设备)
                            ctx.sendPacket(
                                S2CPacketUpdateMyInfo(
                                    packet.name,
                                    manager.base.dataManager.getUserRank(packet.name),
                                    manager.base.dataManager.getUserPremium(packet.name)
                                )
                            )

                            // 登出其他客户端
                            manager.clients
                                .filter { (_, channel) ->
                                    channel
                                        .takeIf { it.hasAttr(AttributeKeys.CURRENT_USER) }
                                        ?.attr(AttributeKeys.CURRENT_USER)
                                        ?.get() == packet.name
                                }
                                .onEach { (_, channel) ->
                                    channel.sendPacket(
                                        S2CPacketUpdateMyInfo(
                                            "",
                                            "",
                                            S2CPacketUpdateMyInfo.Premium.GUEST
                                        )
                                    )
                                    channel.attr(AttributeKeys.CURRENT_USER).remove()
                                }

                            // 登录成功
                            ctx.attr(AttributeKeys.CURRENT_USER).set(packet.name)

                            val address = (ctx.channel().remoteAddress() as? InetSocketAddress)?.let { "${it.address.hostAddress}:${it.port}"  } ?: "unknown"

                            manager.logger.info("[+] ${packet.name}  ($address ${packet.deviceId})")

                            throw ExceptionLoginResult(LoginResult.SUCCESS)
                        }
                            .onFailure { t ->
                                when (t) {
                                    is ExceptionLoginResult -> ctx.sendPacket(
                                        S2CPacketLoginResult(t.result)
                                    )
                                    else -> ctx.sendPacket(
                                        S2CPacketMessageSystem("系统错误: ${t.message}")
                                    )
                                }
                            }
                    }
                    is C2SPacketChatPublic -> {
                        if (!ctx.hasAttr(AttributeKeys.CURRENT_USER)) return@packetHandle

                        val user = ctx.attr(AttributeKeys.CURRENT_USER).get()

                        val boardCastPacket = S2CPacketMessagePublic(
                            ctx.attr(AttributeKeys.CURRENT_USER).get(),
                            UserInfoData(
                                manager.base.dataManager.getUserRank(user) ?: "",
                                ctx.attr(AttributeKeys.GAME_INFO).get()
                            ),
                            packet.message
                                .replace("&", "§")
                                .replace("\n", "")
                                .replace("\r", "")
                        )

                        manager.logger.info("[${boardCastPacket.name}] ${boardCastPacket.message}")

                        manager.clients
                            .values
                            .filter { channel -> channel.hasAttr(AttributeKeys.CURRENT_USER) }
                            .onEach { channel ->
                                // 同时也会发送给发送者客户端上 这不是bug 而是刻意这么设计的
                                channel.sendPacket(boardCastPacket)
                            }
                    }
                    is C2SPacketChatPrivate -> {
                        if (!ctx.hasAttr(AttributeKeys.CURRENT_USER)) return@packetHandle

                        val user = ctx.attr(AttributeKeys.CURRENT_USER).get()

                        manager.clients.values
                            .filter { channel -> channel.hasAttr(AttributeKeys.CURRENT_USER) }
                            .filter { channel -> channel.attr(AttributeKeys.CURRENT_USER).get() == packet.user }
                            .also {
                                // 接收方不在线
                                if (it.isEmpty()) {
                                    ctx.sendPacket(S2CPacketMessagePrivateResult(packet.user, packet.message, false))
                                    return@packetHandle
                                }
                            }
                            .also {
                                val boardCastPacket = S2CPacketMessagePrivate(
                                    ctx.attr(AttributeKeys.CURRENT_USER).get(),
                                    UserInfoData(
                                        manager.base.dataManager.getUserRank(user) ?: "",
                                        ctx.attr(AttributeKeys.GAME_INFO).get()
                                    ),
                                    packet.message
                                        .replace("&", "§")
                                        .replace("\n", "")
                                        .replace("\r", "")
                                )

                                // 兼容多客户端登录 (适用于 同一设备多开客户端)
                                it.onEach { channel ->
                                    channel.sendPacket(boardCastPacket)
                                }

                                ctx.sendPacket(S2CPacketMessagePrivateResult(packet.user, packet.message, true))
                            }
                    }
                    is C2SPacketCommand -> {
                        if (!ctx.hasAttr(AttributeKeys.CURRENT_USER)) return@packetHandle

                        manager.base.commandManager.handle(ctx, packet.root, packet.args.toMutableList())
                    }
                    is C2SPacketUpdateGameInfo -> {
                        if (!ctx.hasAttr(AttributeKeys.CURRENT_USER)) return@packetHandle

                        ctx.attr(AttributeKeys.GAME_INFO).set(packet.info)

                        val name = ctx.attr(AttributeKeys.CURRENT_USER).get()
                        val rank = manager.base.dataManager.getUserRank(name) ?: return@packetHandle

                        manager.clients.values
                            .filter { channel -> channel.hasAttr(AttributeKeys.CURRENT_USER) }
                            .onEach { channel ->
                                channel.sendPacket(
                                    S2CPacketUpdateOtherInfo(
                                        name,
                                        UserInfoData(
                                            rank,
                                            channel.attr(AttributeKeys.GAME_INFO).get()
                                        )
                                    )
                                )
                            }
                    }
                    is C2SPacketQueryUsers -> {
                        if (!ctx.hasAttr(AttributeKeys.CURRENT_USER)) return@packetHandle

                        val users = HashMap<String, UserInfoData>()

                        manager.clients.values
                            .filter { channel -> channel.hasAttr(AttributeKeys.CURRENT_USER) }
                            .filter { channel ->
                                if (packet.onlySameServer) {
                                    channel.attr(AttributeKeys.GAME_INFO).get().server == ctx.attr(AttributeKeys.GAME_INFO).get().server
                                } else {
                                    true
                                }
                            }
                            .onEach { channel ->
                                run queryUser@{
                                    val name = channel.attr(AttributeKeys.CURRENT_USER).get()

                                    users[name] = UserInfoData(
                                        manager.base.dataManager.getUserRank(name) ?: return@queryUser,
                                        channel.attr(AttributeKeys.GAME_INFO).get()
                                    )
                                }
                            }
                            .also {
                                // 告诉客户端这个 id 是 irc 内用户
                                ctx.sendPacket(
                                    S2CPacketUpdateMultiUserInfo(
                                        packet.onlySameServer,
                                        true,
                                        users
                                    )
                                )
                            }
                    }
                    else -> { }
                }
            }
        }
    }
}