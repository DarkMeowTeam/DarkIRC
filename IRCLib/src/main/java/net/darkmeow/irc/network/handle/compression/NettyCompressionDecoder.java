package net.darkmeow.irc.network.handle.compression;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.DecoderException;
import net.darkmeow.irc.network.FriendBuffer;

import java.util.List;
import java.util.zip.Inflater;

public class NettyCompressionDecoder extends ByteToMessageDecoder {
    private final Inflater inflater = new Inflater();
    private int threshold;

    public NettyCompressionDecoder(int thresholdIn) {
        this.threshold = thresholdIn;
    }

    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() != 0) {
            FriendBuffer buffer = new FriendBuffer(in);
            int i = buffer.readVarInt();

            if (i == 0) {
                out.add(buffer.readBytes(buffer.readableBytes()));
            } else {
                if (i < this.threshold) {
                    throw new DecoderException("Badly compressed packet - size of " + i + " is below server threshold of " + this.threshold);
                }

                byte[] compressedData = new byte[buffer.readableBytes()];
                buffer.readBytes(compressedData);
                this.inflater.setInput(compressedData);
                byte[] uncompressedData = new byte[i];
                this.inflater.inflate(uncompressedData);
                out.add(Unpooled.wrappedBuffer(uncompressedData));
                this.inflater.reset();
            }
        }
    }

    public void setCompressionThreshold(int thresholdIn) {
        this.threshold = thresholdIn;
    }
}
