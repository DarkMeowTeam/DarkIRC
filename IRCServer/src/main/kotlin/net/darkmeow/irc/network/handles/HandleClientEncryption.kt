package net.darkmeow.irc.network.handles

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerAdapter
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelPromise
import io.netty.handler.codec.LengthFieldBasedFrameDecoder
import io.netty.util.CharsetUtil
import net.darkmeow.irc.network.IRCNetworkBaseConfig
import net.darkmeow.irc.network.NetworkManager
import net.darkmeow.irc.utils.EncryptUtils

class HandleClientEncryption(private val manager: NetworkManager): ChannelHandlerAdapter() {

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

        super.channelActive(ctx)
    }

    override fun write(ctx: ChannelHandlerContext, data: Any, promise: ChannelPromise) {
        if (data is String) {
            val buffer = Unpooled.buffer()

            val encryptedData = EncryptUtils.encrypt(data, manager.base.configManager.configs.key).toByteArray(CharsetUtil.UTF_8)

            buffer.writeInt(encryptedData.size)
            buffer.writeBytes(encryptedData)

            super.write(ctx, buffer, promise)
        }
    }

    override fun channelRead(ctx: ChannelHandlerContext, data: Any) {
        if (data is ByteBuf) {
            super.channelRead(ctx, EncryptUtils.decrypt(data.toString(CharsetUtil.UTF_8), manager.base.configManager.configs.key))
        }
    }

}