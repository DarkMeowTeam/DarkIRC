package net.darkmeow.irc.client.network.handle;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.EventLoopGroup;
import net.darkmeow.irc.client.AttributeKeys;
import net.darkmeow.irc.client.network.IRCClientConnection;

import java.util.UUID;

public class HandleClientConnection extends ChannelInboundHandlerAdapter {

    public final IRCClientConnection client;

    public final EventLoopGroup group;

    public HandleClientConnection(IRCClientConnection client, EventLoopGroup group) {
        this.client = client;
        this.group = group;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        final UUID uniqueID = UUID.randomUUID();

        ctx.channel().attr(AttributeKeys.UUID).set(uniqueID);

        client.channel = ctx.channel();
        client.channelUniqueId = uniqueID;

        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        final UUID uniqueID = ctx.channel().attr(AttributeKeys.UUID).get();

        if (client.channelUniqueId == uniqueID && client.channel != null) {
            client.channel = null;
            client.channelUniqueId = null;

            new Thread(() -> {
                client.base.userManager.reset();

                group.shutdownGracefully().syncUninterruptibly();
                client.base.listenable.onDisconnect(
                    client.base.resultManager.disconnectType,
                    client.base.resultManager.disconnectReason,
                    client.base.resultManager.disconnectLogout
                );
            }).start();
        }

        ctx.channel().attr(AttributeKeys.UUID).set(null);

        super.channelInactive(ctx);
    }

}
