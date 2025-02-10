package net.darkmeow.irc.network.handles.encryption

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.util.CharsetUtil
import net.darkmeow.irc.network.NetworkManager
import net.darkmeow.irc.utils.EncryptUtils
import javax.crypto.BadPaddingException

class HandleClientEncryptionInbound(private val manager: NetworkManager): ChannelInboundHandlerAdapter() {

    override fun channelRead(ctx: ChannelHandlerContext, data: Any) {
        if (data is ByteBuf) {
            runCatching {
                super.channelRead(
                    ctx,
                    EncryptUtils.decrypt(data.toString(CharsetUtil.UTF_8), manager.base.configManager.configs.ircServer.key)
                )
            }
                .onFailure { e ->
                    if (e !is BadPaddingException && e !is IllegalArgumentException) {
                        throw e
                    }
                }
                .also {
                    data.release()
                }
        }
    }

}