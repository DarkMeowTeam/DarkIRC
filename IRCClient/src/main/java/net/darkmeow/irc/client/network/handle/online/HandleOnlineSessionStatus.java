package net.darkmeow.irc.client.network.handle.online;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.darkmeow.irc.client.data.DataOtherSessionInfo;
import net.darkmeow.irc.client.network.IRCClientNetworkManager;
import net.darkmeow.irc.network.packet.online.s2c.S2CPacketUpdateSessionState;
import net.darkmeow.irc.network.packet.online.s2c.S2CPacketUpdateSessionStateMulti;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.UUID;

public final class HandleOnlineSessionStatus extends ChannelInboundHandlerAdapter {

    @NotNull
    public final IRCClientNetworkManager connection;

    public HandleOnlineSessionStatus(@NotNull IRCClientNetworkManager connection) {
        this.connection = connection;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object packet) throws Exception {
        if (packet instanceof S2CPacketUpdateSessionState) {
            handleUpdateOtherState((S2CPacketUpdateSessionState) packet);
        } else if (packet instanceof S2CPacketUpdateSessionStateMulti) {
            handleUpdateOtherStateMulti((S2CPacketUpdateSessionStateMulti) packet);
        } else {
            super.channelRead(ctx, packet);
        }
    }

    public void handleUpdateOtherState(@NotNull S2CPacketUpdateSessionState packet) {
        if (packet.getUser() == null) {
            connection.base.sessionManager.users.remove(packet.getId());
        } else {
            connection.base.sessionManager.users.computeIfAbsent(packet.getId(), DataOtherSessionInfo::new).update(packet.getUser());
        }
    }

    public void handleUpdateOtherStateMulti(@NotNull S2CPacketUpdateSessionStateMulti packet) {
        final ArrayList<UUID> updates = new ArrayList<>();

        packet.getUserMap().forEach((uuid, info) -> {
            connection.base.sessionManager.users.computeIfAbsent(uuid, DataOtherSessionInfo::new).update(info);
            updates.add(uuid);
        });

        if (packet.isOverrideAll()) {
            connection.base.sessionManager.users.forEach((uuid, info) -> {
                if (!updates.contains(uuid)) {
                    info.markInvalid();
                }
            });
            connection.base.sessionManager.clearInvalidUsers();
        }
    }

}
