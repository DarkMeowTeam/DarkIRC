package net.darkmeow.irc.network.handle.compression;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import net.darkmeow.irc.network.FriendBuffer;

import java.util.zip.Deflater;

public class NettyCompressionEncoder extends MessageToByteEncoder<ByteBuf> {
    private final byte[] buffer = new byte[8192];
    private final Deflater deflater = new Deflater();
    private int threshold;

    public NettyCompressionEncoder(int thresholdIn) {
        this.threshold = thresholdIn;
    }

    protected void encode(ChannelHandlerContext ctx, ByteBuf in, ByteBuf out) {
        int i = in.readableBytes();
        FriendBuffer buffer = new FriendBuffer(out);

        if (i < this.threshold) {
            buffer.writeVarInt(0);
            buffer.writeBytes(in);
        } else {
            byte[] rawData = new byte[i];
            in.readBytes(rawData);
            buffer.writeVarInt(rawData.length);
            this.deflater.setInput(rawData, 0, i);
            this.deflater.finish();

            while (!this.deflater.finished()) {
                int j = this.deflater.deflate(this.buffer);
                buffer.writeBytes(this.buffer, 0, j);
            }

            this.deflater.reset();
        }
    }

    public void setCompressionThreshold(int thresholdIn) {
        this.threshold = thresholdIn;
    }
}