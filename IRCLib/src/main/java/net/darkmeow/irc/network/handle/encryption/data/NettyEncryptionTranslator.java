package net.darkmeow.irc.network.handle.encryption.data;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import org.jetbrains.annotations.NotNull;

import javax.crypto.Cipher;
import javax.crypto.ShortBufferException;

public class NettyEncryptionTranslator
{
    @NotNull
    private final Cipher cipher;
    private byte[] inputBuffer = new byte[0];
    private byte[] outputBuffer = new byte[0];

    public NettyEncryptionTranslator(@NotNull Cipher cipherIn)
    {
        this.cipher = cipherIn;
    }

    private byte[] bufToBytes(ByteBuf buf)
    {
        int i = buf.readableBytes();

        if (this.inputBuffer.length < i)
        {
            this.inputBuffer = new byte[i];
        }

        buf.readBytes(this.inputBuffer, 0, i);
        return this.inputBuffer;
    }

    public ByteBuf decipher(ChannelHandlerContext ctx, ByteBuf buffer) throws ShortBufferException
    {
        int i = buffer.readableBytes();
        byte[] source = this.bufToBytes(buffer);
        ByteBuf bytebuf = ctx.alloc().heapBuffer(this.cipher.getOutputSize(i));
        bytebuf.writerIndex(this.cipher.update(source, 0, i, bytebuf.array(), bytebuf.arrayOffset()));
        return bytebuf;
    }

    public void cipher(ByteBuf in, ByteBuf out) throws ShortBufferException
    {
        int i = in.readableBytes();
        byte[] source = this.bufToBytes(in);
        int j = this.cipher.getOutputSize(i);

        if (this.outputBuffer.length < j)
        {
            this.outputBuffer = new byte[j];
        }

        out.writeBytes(this.outputBuffer, 0, this.cipher.update(source, 0, i, this.outputBuffer));
    }
}