package net.darkmeow.irc.network.handles.online

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import net.darkmeow.irc.data.enmus.EnumChatType
import net.darkmeow.irc.data.enmus.EnumUserPremium
import net.darkmeow.irc.data.input.DataSessionInputStatusPrivate
import net.darkmeow.irc.data.input.DataSessionInputStatusPublic
import net.darkmeow.irc.network.IRCNetworkManagerServer
import net.darkmeow.irc.network.packet.online.c2s.C2SPacketInputStatus
import net.darkmeow.irc.network.packet.online.s2c.S2CPacketOtherInputState

class HandlePacketInputStatus(private val connection: IRCNetworkManagerServer): SimpleChannelInboundHandler<C2SPacketInputStatus>() {

    companion object {
        const val INPUT_TIME_OUT = 150000
    }


    override fun channelRead0(ctx: ChannelHandlerContext, packet: C2SPacketInputStatus) {
        if (connection.userPremium.ordinal < EnumUserPremium.USER.ordinal) return

        val newState = when (packet.type) {
            C2SPacketInputStatus.Type.PUBLIC -> DataSessionInputStatusPublic()
            C2SPacketInputStatus.Type.PRIVATE -> DataSessionInputStatusPrivate(packet.receiver)
            C2SPacketInputStatus.Type.CLEAR -> null
        }

        if (connection.inputStatus != newState) {
            connection.inputStatus = newState
            syncInputStatus()
        }
    }

    override fun channelInactive(ctx: ChannelHandlerContext) {
        syncInputStatus()
        super.channelInactive(ctx)
    }

    /**
     * 向所有客户端同步正在输入状态
     */
    fun syncInputStatus() {
        connection.bossNetworkManager.clients.values.forEach { otherCall ->
            val publicInputs = connection.bossNetworkManager.clients.values
                .mapNotNull { otherInput ->
                    otherInput.inputStatus
                        ?.takeIf { !otherInput.currentIsInvisible }
                        ?.takeIf { it.timestamp + INPUT_TIME_OUT > System.currentTimeMillis() }
                        ?.takeIf { it.type == EnumChatType.PUBLIC }
                        ?.let { otherInput.sessionId }
                }
                .toMutableSet()

            val privateInputs = connection.bossNetworkManager.clients.values
                .mapNotNull { otherInput ->
                    otherInput.inputStatus
                        // 忽略隐身状态上报
                        ?.takeIf { !otherInput.currentIsInvisible }
                        // 忽略输入状态超时
                        ?.takeIf { it.timestamp + INPUT_TIME_OUT > System.currentTimeMillis() }
                        ?.takeIf { it.type == EnumChatType.PRIVATE }
                        ?.takeIf {
                            val receiver = (it as DataSessionInputStatusPrivate).receiver

                            return@takeIf receiver == otherCall.sessionId.toString() || receiver == otherCall.user
                        }
                        ?.let { otherInput.sessionId }
                }
                .toMutableSet()


            if (otherCall.syncInputStatus.shouldUpdate(publicInputs, privateInputs)) {
                otherCall.sendPacket(S2CPacketOtherInputState(publicInputs, privateInputs))
                otherCall.syncInputStatus.update(publicInputs, privateInputs)
            }
        }
    }
}