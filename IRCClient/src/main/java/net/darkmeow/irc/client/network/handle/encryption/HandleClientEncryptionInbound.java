package net.darkmeow.irc.client.network.handle.encryption;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import net.darkmeow.irc.client.network.IRCClientConnection;
import net.darkmeow.irc.utils.EncryptUtils;

public class HandleClientEncryptionInbound extends ChannelInboundHandlerAdapter {

    public final IRCClientConnection client;

    public HandleClientEncryptionInbound(IRCClientConnection client) {
        this.client = client;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object data) throws Exception {
        if (data instanceof ByteBuf) {
            super.channelRead(ctx, EncryptUtils.decrypt(((ByteBuf) data).toString(CharsetUtil.UTF_8), client.key));
        }
    }

}
