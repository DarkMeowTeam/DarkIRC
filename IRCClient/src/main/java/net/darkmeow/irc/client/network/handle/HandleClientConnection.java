package net.darkmeow.irc.client.network.handle;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.EventLoopGroup;
import net.darkmeow.irc.client.AttributeKeys;
import net.darkmeow.irc.client.network.IRCClientConnection;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;

public class HandleClientConnection extends ChannelInboundHandlerAdapter {

    @NotNull
    public final IRCClientConnection client;

    @NotNull
    public final EventLoopGroup group;

    @NotNull
    private final CountDownLatch channelActiveLatch;

    public HandleClientConnection(@NotNull IRCClientConnection client, @NotNull EventLoopGroup group, @NotNull CountDownLatch channelActiveLatch) {
        this.client = client;
        this.group = group;
        this.channelActiveLatch = channelActiveLatch;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        final UUID uniqueID = UUID.randomUUID();

        ctx.channel().attr(AttributeKeys.UUID).set(uniqueID);

        client.channel = ctx.channel();
        client.channelUniqueId = uniqueID;

        channelActiveLatch.countDown();

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

                client.base.listenable.onDisconnect(
                    client.base.resultManager.disconnectType,
                    client.base.resultManager.disconnectReason,
                    client.base.resultManager.disconnectLogout
                );

                group.shutdownGracefully().syncUninterruptibly();
            }).start();
        }

        ctx.channel().attr(AttributeKeys.UUID).set(null);

        super.channelInactive(ctx);
    }

}
