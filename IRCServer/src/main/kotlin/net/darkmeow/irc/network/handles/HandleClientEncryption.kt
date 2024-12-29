package net.darkmeow.irc.network.handles

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerAdapter
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelPromise
import io.netty.util.CharsetUtil
import net.darkmeow.irc.network.NetworkManager
import net.darkmeow.irc.utils.EncryptUtils

class HandleClientEncryption(private val manager: NetworkManager): ChannelHandlerAdapter() {

    override fun write(ctx: ChannelHandlerContext, data: Any, promise: ChannelPromise) {
        if (data is String) {
            super.write(ctx, Unpooled.copiedBuffer(EncryptUtils.encrypt(data, manager.base.configManager.configs.key), CharsetUtil.UTF_8), promise)
        }
    }

    override fun channelRead(ctx: ChannelHandlerContext, data: Any) {
        if (data is ByteBuf) {
            super.channelRead(ctx, EncryptUtils.decrypt(data.toString(CharsetUtil.UTF_8), manager.base.configManager.configs.key))
        }
    }

}