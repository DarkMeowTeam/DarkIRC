package net.darkmeow.irc.network.handle.encryption;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import net.darkmeow.irc.network.handle.encryption.data.NettyEncryptionTranslator;
import org.jetbrains.annotations.NotNull;

import javax.crypto.Cipher;
import java.util.List;

public class NettyEncryptingDecoder extends MessageToMessageDecoder<ByteBuf>
{
    @NotNull
    private final NettyEncryptionTranslator decryptionCodec;

    public NettyEncryptingDecoder(@NotNull Cipher cipher)
    {
        this.decryptionCodec = new NettyEncryptionTranslator(cipher);
    }

    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception
    {
        out.add(this.decryptionCodec.decipher(ctx, in));
    }
}