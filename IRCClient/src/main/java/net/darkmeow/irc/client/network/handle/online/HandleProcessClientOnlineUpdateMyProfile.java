package net.darkmeow.irc.client.network.handle.online;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.Getter;
import net.darkmeow.irc.client.data.DataSelfSessionInfo;
import net.darkmeow.irc.client.network.IRCClientNetworkManager;
import net.darkmeow.irc.network.packet.online.s2c.S2CPacketUpdateMyProfile;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class HandleProcessClientOnlineUpdateMyProfile extends SimpleChannelInboundHandler<S2CPacketUpdateMyProfile> {

    @NotNull
    public final IRCClientNetworkManager connection;

    @Getter
    private int updateCount = 0;

    public HandleProcessClientOnlineUpdateMyProfile(@NotNull IRCClientNetworkManager connection) {
        this.connection = connection;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, S2CPacketUpdateMyProfile packet) {
        updateCount++;

        if (connection.base.sessionManager.self == null || connection.base.sessionManager.self.uniqueId != connection.base.sessionManager.sessionId) {
            if (connection.base.sessionManager.self != null) {
                connection.base.sessionManager.self.markInvalid();
            }
            connection.base.sessionManager.self = new DataSelfSessionInfo(
                Objects.requireNonNull(connection.base.sessionManager.sessionId), // 正常使用时不可能为 null
                packet.getName(),
                packet.getPremium(),
                packet.isInvisible()
            );
        } else {
            connection.base.sessionManager.self.update(packet.getName(), packet.getPremium(), packet.isInvisible());
        }

        this.connection.base.listenable.onUpdateUserInfo(connection.base.sessionManager.self, updateCount == 1);
    }

}
