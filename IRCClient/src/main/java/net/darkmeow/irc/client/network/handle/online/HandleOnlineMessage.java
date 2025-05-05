package net.darkmeow.irc.client.network.handle.online;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.darkmeow.irc.client.data.DataOtherSessionInfo;
import net.darkmeow.irc.client.network.IRCClientNetworkManager;
import net.darkmeow.irc.network.packet.online.s2c.S2CPacketPrivateMessageResult;
import net.darkmeow.irc.network.packet.online.s2c.S2CPacketSessionMessage;
import net.darkmeow.irc.network.packet.online.s2c.S2CPacketSystemMessage;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class HandleOnlineMessage extends ChannelInboundHandlerAdapter {

    @NotNull
    public final IRCClientNetworkManager connection;

    public HandleOnlineMessage(@NotNull IRCClientNetworkManager connection) {
        this.connection = connection;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object packet) throws Exception {
        if (packet instanceof S2CPacketSessionMessage) {
            handleSessionMessage((S2CPacketSessionMessage) packet);
        } else if (packet instanceof S2CPacketSystemMessage) {
            handleSystemMessage((S2CPacketSystemMessage) packet);
        } else if (packet instanceof S2CPacketPrivateMessageResult) {
            handlePrivateMessageResult((S2CPacketPrivateMessageResult) packet);
        } else {
            super.channelRead(ctx, packet);
        }
    }

    public void handleSessionMessage(@NotNull S2CPacketSessionMessage packet) {
        final DataOtherSessionInfo info = connection.base.sessionManager.users.computeIfAbsent(packet.getSender(), DataOtherSessionInfo::new);
        info.update(packet.getSenderData());

        switch (packet.getType()) {
            case PUBLIC:
                connection.base.listenable.onMessagePublic(info, packet.getMessage());
                break;
            case PRIVATE:
                connection.base.listenable.onMessagePrivate(info, packet.getMessage());
                break;
        }

    }

    public void handleSystemMessage(@NotNull S2CPacketSystemMessage packet) {
        connection.base.listenable.onMessageSystem(packet.getMessage());
    }

    public void handlePrivateMessageResult(@NotNull S2CPacketPrivateMessageResult packet) {
        if (packet.isSuccess()) {
            connection.base.listenable.onPrivateMessageSendSuccess(packet.getReceiver(), Objects.requireNonNull(packet.getMessage()));
        } else {
            connection.base.listenable.onPrivateMessageSendFailed(packet.getReceiver());
        }
    }

}
