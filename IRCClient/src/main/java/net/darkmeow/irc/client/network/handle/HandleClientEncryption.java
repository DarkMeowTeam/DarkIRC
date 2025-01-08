package net.darkmeow.irc.client.network.handle;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import net.darkmeow.irc.client.network.IRCClientConnection;
import net.darkmeow.irc.client.network.handle.encryption.HandleClientEncryptionInbound;
import net.darkmeow.irc.client.network.handle.encryption.HandleClientEncryptionOutbound;
import net.darkmeow.irc.network.IRCNetworkBaseConfig;

public class HandleClientEncryption extends ChannelInboundHandlerAdapter {

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
        ctx.pipeline().addAfter(
            "BaseFrameDecoder",
            "BaseEncryptionInbound",
            new HandleClientEncryptionInbound(client)
        );
        ctx.pipeline().addAfter(
            "BaseFrameDecoder",
            "BaseEncryptionOutbound",
            new HandleClientEncryptionOutbound(client)
        );

        super.channelActive(ctx);
    }

}
