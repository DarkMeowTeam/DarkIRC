package net.darkmeow.irc.client.network.handle;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.darkmeow.irc.client.network.IRCClientConnection;

public class HandleClientConnection extends ChannelInboundHandlerAdapter {

    public final IRCClientConnection client;

    public HandleClientConnection(IRCClientConnection client) {
        this.client = client;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        client.channel = ctx.channel();

        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        client.channel = null;

        new Thread(() -> client.base.listenable.onDisconnect(
            client.base.resultManager.disconnectType,
            client.base.resultManager.disconnectReason,
            client.base.resultManager.disconnectLogout
        )).start();

        super.channelInactive(ctx);
    }

}
