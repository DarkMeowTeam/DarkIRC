package net.darkmeow.irc.network.handles.encryption

import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelOutboundHandlerAdapter
import io.netty.channel.ChannelPromise
import io.netty.util.CharsetUtil
import net.darkmeow.irc.network.NetworkManager
import net.darkmeow.irc.utils.EncryptUtils

class HandleClientEncryptionOutbound(private val manager: NetworkManager): ChannelOutboundHandlerAdapter() {

    override fun write(ctx: ChannelHandlerContext, data: Any, promise: ChannelPromise) {
        if (data is String) {
            val buffer = Unpooled.buffer()

            val encryptedData = EncryptUtils.encrypt(data, manager.base.configManager.configs.ircServer.key).toByteArray(CharsetUtil.UTF_8)

            buffer.writeInt(encryptedData.size)
            buffer.writeBytes(encryptedData)

            super.write(ctx, buffer, promise)
        }
    }

}