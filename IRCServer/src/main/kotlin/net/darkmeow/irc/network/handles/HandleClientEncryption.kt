package net.darkmeow.irc.network.handles

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.handler.codec.LengthFieldBasedFrameDecoder
import net.darkmeow.irc.network.IRCNetworkBaseConfig
import net.darkmeow.irc.network.NetworkManager
import net.darkmeow.irc.network.handles.encryption.HandleClientEncryptionInbound
import net.darkmeow.irc.network.handles.encryption.HandleClientEncryptionOutbound

class HandleClientEncryption(private val manager: NetworkManager): ChannelInboundHandlerAdapter() {

    override fun channelActive(ctx: ChannelHandlerContext) {
        ctx.pipeline().addBefore(
            "BaseEncryption",
            "BaseFrameDecoder",
            LengthFieldBasedFrameDecoder(
                IRCNetworkBaseConfig.MAX_FRAME_LENGTH,
                0,
                4,
                0,
                4
            )
        )
        ctx.pipeline().addAfter(
            "BaseFrameDecoder",
            "BaseEncryptionInbound",
            HandleClientEncryptionInbound(manager)
        )
        ctx.pipeline().addAfter(
            "BaseFrameDecoder",
            "BaseEncryptionOutbound",
            HandleClientEncryptionOutbound(manager)
        )

        super.channelActive(ctx)
    }

}