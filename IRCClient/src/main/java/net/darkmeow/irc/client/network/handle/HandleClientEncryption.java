package net.darkmeow.irc.client.network.handle;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.util.CharsetUtil;
import net.darkmeow.irc.client.network.IRCClientConnection;
import net.darkmeow.irc.network.IRCNetworkBaseConfig;
import net.darkmeow.irc.utils.EncryptUtils;

public class HandleClientEncryption extends ChannelHandlerAdapter {

    public final IRCClientConnection client;

    public HandleClientEncryption(IRCClientConnection client) {
        this.client = client;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.pipeline().addBefore("BaseEncryption", "BaseFrameDecoder", new LengthFieldBasedFrameDecoder(
            IRCNetworkBaseConfig.MAX_FRAME_LENGTH,
            0,
            4,
            0,
            4
        ));

        super.channelActive(ctx);
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

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object data) throws Exception {
        if (data instanceof ByteBuf) {
            super.channelRead(ctx, EncryptUtils.decrypt(((ByteBuf) data).toString(CharsetUtil.UTF_8), client.key));
        }
    }

}
