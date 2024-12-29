package net.darkmeow.irc.client.network.handle;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.util.CharsetUtil;
import net.darkmeow.irc.client.network.IRCClientConnection;
import net.darkmeow.irc.utils.EncryptUtils;

public class HandleClientEncryption extends ChannelHandlerAdapter {

    public final IRCClientConnection client;

    public HandleClientEncryption(IRCClientConnection client) {
        this.client = client;
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object data, ChannelPromise promise) throws Exception {
        if (data instanceof String) {
            super.write(ctx, Unpooled.copiedBuffer(EncryptUtils.encrypt((String) data, client.key), CharsetUtil.UTF_8), promise);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object data) throws Exception {
        if (data instanceof ByteBuf) {
            super.channelRead(ctx, EncryptUtils.decrypt(((ByteBuf) data).toString(CharsetUtil.UTF_8), client.key));
        }
    }

}
