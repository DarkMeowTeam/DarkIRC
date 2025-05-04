package net.darkmeow.irc.network.handle.encryption;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import net.darkmeow.irc.network.handle.encryption.data.NettyEncryptionTranslator;
import org.jetbrains.annotations.NotNull;

import javax.crypto.Cipher;

public class NettyEncryptingEncoder extends MessageToByteEncoder<ByteBuf>
{
    @NotNull
    private final NettyEncryptionTranslator encryptionCodec;

    public NettyEncryptingEncoder(@NotNull Cipher cipher)
    {
        this.encryptionCodec = new NettyEncryptionTranslator(cipher);
    }

    protected void encode(ChannelHandlerContext ctx, ByteBuf in, ByteBuf out) throws Exception
    {
        this.encryptionCodec.cipher(in, out);
    }
}