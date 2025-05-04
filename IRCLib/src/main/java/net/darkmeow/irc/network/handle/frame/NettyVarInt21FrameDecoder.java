package net.darkmeow.irc.network.handle.frame;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;
import net.darkmeow.irc.network.FriendBuffer;
import net.darkmeow.irc.network.IRCNetworkBaseConfig;

import java.util.List;

public class NettyVarInt21FrameDecoder extends ByteToMessageDecoder
{

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        in.markReaderIndex();
        byte[] headerBytes = new byte[IRCNetworkBaseConfig.VAR_INT_FRAME_LENGTH];

        for (int i = 0; i < headerBytes.length; ++i) {
            if (!in.isReadable()) {
                in.resetReaderIndex();
                return;
            }

            headerBytes[i] = in.readByte();

            if (headerBytes[i] >= 0) {
                FriendBuffer buffer = new FriendBuffer(Unpooled.wrappedBuffer(headerBytes));

                try {
                    int length = buffer.readVarInt();

                    if (in.readableBytes() >= length) {
                        out.add(in.readBytes(length));
                        return;
                    }

                    in.resetReaderIndex();
                } finally {
                    buffer.release();
                }

                return;
            }
        }

        throw new CorruptedFrameException("Frame length exceeds 21-bit range.");
    }

}