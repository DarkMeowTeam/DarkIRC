package net.darkmeow.irc.network.handle.frame;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import net.darkmeow.irc.network.FriendBuffer;
import net.darkmeow.irc.network.IRCNetworkBaseConfig;

public class NettyVarInt21FrameEncoder extends MessageToByteEncoder<ByteBuf>
{

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf in, ByteBuf out) {
        int readableBytes = in.readableBytes();
        int varIntSize = FriendBuffer.getVarIntSize(readableBytes);

        if (varIntSize > IRCNetworkBaseConfig.VAR_INT_FRAME_LENGTH) {
            throw new IllegalArgumentException("Unable to fit " + readableBytes + " bytes into " + IRCNetworkBaseConfig.VAR_INT_FRAME_LENGTH);
        } else {
            FriendBuffer buffer = new FriendBuffer(out);
            buffer.ensureWritable(varIntSize + readableBytes);
            buffer.writeVarInt(readableBytes);
            buffer.writeBytes(in, in.readerIndex(), readableBytes);
        }
    }

}