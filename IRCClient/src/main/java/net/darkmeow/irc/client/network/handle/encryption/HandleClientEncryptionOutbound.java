package net.darkmeow.irc.client.network.handle.encryption;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.util.CharsetUtil;
import net.darkmeow.irc.client.network.IRCClientConnection;
import net.darkmeow.irc.utils.EncryptUtils;

public class HandleClientEncryptionOutbound extends ChannelOutboundHandlerAdapter {

    public final IRCClientConnection client;

    public HandleClientEncryptionOutbound(IRCClientConnection client) {
        this.client = client;
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object data, ChannelPromise promise) throws Exception {
        if (data instanceof String) {
            ByteBuf buffer = Unpooled.buffer();

            byte[] encryptedData = EncryptUtils.encrypt((String) data, client.key).getBytes(CharsetUtil.UTF_8);

            buffer.writeInt(encryptedData.length);
            buffer.writeBytes(encryptedData);

            super.write(ctx, buffer, promise);
        }
    }

}
