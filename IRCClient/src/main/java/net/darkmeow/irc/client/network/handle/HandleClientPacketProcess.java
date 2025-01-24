package net.darkmeow.irc.client.network.handle;

import com.google.gson.JsonParser;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.darkmeow.irc.client.AttributeKeys;
import net.darkmeow.irc.client.data.IRCResultSendMessageToPrivate;
import net.darkmeow.irc.client.data.DataSelfSessionInfo;
import net.darkmeow.irc.client.data.DataOtherSessionInfo;
import net.darkmeow.irc.client.enums.EnumDisconnectType;
import net.darkmeow.irc.client.enums.EnumPremium;
import net.darkmeow.irc.client.enums.EnumResultLogin;
import net.darkmeow.irc.client.network.IRCClientConnection;
import net.darkmeow.irc.network.PacketUtils;
import net.darkmeow.irc.network.packet.c2s.C2SPacketKeepAlive;
import net.darkmeow.irc.network.packet.c2s.C2SPacketQueryUsers;
import net.darkmeow.irc.network.packet.s2c.*;

import java.util.ArrayList;
import java.util.UUID;

public final class HandleClientPacketProcess extends ChannelInboundHandlerAdapter {

    public final IRCClientConnection connection;

    public HandleClientPacketProcess(IRCClientConnection connection) {
        this.connection = connection;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object data) throws Exception {
        if (ctx.channel().attr(AttributeKeys.UUID).get() != connection.channelUniqueId || connection.channel == null) {
            ctx.channel().disconnect();
            return;
        }

        final S2CPacket serverPacket = PacketUtils.resolveServerPacket(JsonParser.parseString(data.toString()).getAsJsonObject());

        if (serverPacket instanceof S2CPacketHandShake) {
            // 握手包 客户端主动发 服务端回应
            connection.base.resultManager.handShakeLatch.countDown();
        } else if (serverPacket instanceof S2CPacketKeepAlive) {
            // 心跳包 服务端发送 客户端回应
            connection.sendPacket(new C2SPacketKeepAlive(((S2CPacketKeepAlive) serverPacket).id), true);
        } else if (serverPacket instanceof S2CPacketLoginResult) {
            final S2CPacketLoginResult packet = (S2CPacketLoginResult) serverPacket;

            if (connection.base.resultManager.loginResultCallback != null) {
                final S2CPacketLoginResult.LoginResult result = packet.result;

                if (result == S2CPacketLoginResult.LoginResult.INVALID_CLIENT) {
                    connection.base.resultManager.loginResultCallback.accept(EnumResultLogin.INVALID_CLIENT);
                } else if (result == S2CPacketLoginResult.LoginResult.OUTDATED_CLIENT_VERSION) {
                    connection.base.resultManager.loginResultCallback.accept(EnumResultLogin.OUTDATED_CLIENT_VERSION);
                } else if (result == S2CPacketLoginResult.LoginResult.USER_OR_PASSWORD_WRONG) {
                    connection.base.resultManager.loginResultCallback.accept(EnumResultLogin.USER_OR_PASSWORD_WRONG);
                } else if (result == S2CPacketLoginResult.LoginResult.NO_PREMIUM_LOGIN_THIS_CLIENT) {
                    connection.base.resultManager.loginResultCallback.accept(EnumResultLogin.NO_PREMIUM_LOGIN_THIS_CLIENT);
                } else if (result == S2CPacketLoginResult.LoginResult.INVALID_TOKEN) {
                    connection.base.resultManager.loginResultCallback.accept(EnumResultLogin.INVALID_TOKEN);
                } else if (result == S2CPacketLoginResult.LoginResult.SUCCESS) {
                    connection.base.resultManager.loginResultCallback.accept(EnumResultLogin.SUCCESS);
                    connection.sendPacket(new C2SPacketQueryUsers(false), true);

                    if (packet.token != null) {
                        connection.base.listenable.onUpdateSession(packet.token);
                    }
                }
            }
        } else if (serverPacket instanceof S2CPacketUpdateMySessionInfo) {
            final S2CPacketUpdateMySessionInfo packet = (S2CPacketUpdateMySessionInfo) serverPacket;
            boolean isFirstLogin;

            if (connection.base.sessionManager.self == null) {
                connection.base.sessionManager.self = new DataSelfSessionInfo(
                    packet.sessionUniqueId,
                    packet.name,
                    packet.rank,
                    EnumPremium.getEnumPremiumFromPacket(packet.premium)
                );
                isFirstLogin = true;
            } else if (connection.base.sessionManager.self.uniqueId != packet.sessionUniqueId || !connection.base.sessionManager.self.isValid()) {
                connection.base.sessionManager.self.markInvalid();
                connection.base.sessionManager.self = new DataSelfSessionInfo(
                    packet.sessionUniqueId,
                    packet.name,
                    packet.rank,
                    EnumPremium.getEnumPremiumFromPacket(packet.premium)
                );
                isFirstLogin = true;
            }else {
                connection.base.sessionManager.self.update(
                    packet.name,
                    packet.rank,
                    EnumPremium.getEnumPremiumFromPacket(packet.premium)
                );
                isFirstLogin = false;
            }

            connection.base.listenable.onUpdateUserInfo(connection.base.sessionManager.self, isFirstLogin);
        } else if (serverPacket instanceof S2CPacketMessagePublic) {
            final S2CPacketMessagePublic packet = (S2CPacketMessagePublic) serverPacket;

            final DataOtherSessionInfo info = connection.base.sessionManager.users.computeIfAbsent(
                packet.sessionUniqueId,
                DataOtherSessionInfo::new
            );
            info.update(packet.info);

            connection.base.listenable.onMessagePublic(info, packet.message);
        } else if (serverPacket instanceof S2CPacketMessagePrivate) {
            final S2CPacketMessagePrivate packet = (S2CPacketMessagePrivate) serverPacket;

            final DataOtherSessionInfo info = connection.base.sessionManager.users.computeIfAbsent(
                packet.sessionUniqueId,
                DataOtherSessionInfo::new
            );
            info.update(packet.info);

            connection.base.listenable.onMessagePrivate(info, packet.message);
        } else if (serverPacket instanceof S2CPacketMessageSystem) {
            connection.base.listenable.onMessageSystem(((S2CPacketMessageSystem) serverPacket).message);
        } else if (serverPacket instanceof S2CPacketMessagePrivateResult) {
            if (connection.base.resultManager.privateResultCallback != null) {
                connection.base.resultManager.privateResultCallback.accept(
                    new IRCResultSendMessageToPrivate(
                        ((S2CPacketMessagePrivateResult) serverPacket).success,
                        ((S2CPacketMessagePrivateResult) serverPacket).name,
                        ((S2CPacketMessagePrivateResult) serverPacket).message
                    )
                );
            }
        } else if (serverPacket instanceof S2CPacketUpdateOtherSessionInfo) {
            final S2CPacketUpdateOtherSessionInfo packet = (S2CPacketUpdateOtherSessionInfo) serverPacket;

            if (packet.info == null) {
                connection.base.sessionManager.users.remove(((S2CPacketUpdateOtherSessionInfo) serverPacket).sessionUniqueId);
            } else {
                connection.base.sessionManager.users.computeIfAbsent(
                    ((S2CPacketUpdateOtherSessionInfo) serverPacket).sessionUniqueId,
                    DataOtherSessionInfo::new
                ).update(packet.info);
            }
        } else if (serverPacket instanceof S2CPacketUpdateMultiSessionInfo) {
            final S2CPacketUpdateMultiSessionInfo packet = (S2CPacketUpdateMultiSessionInfo) serverPacket;
            final ArrayList<UUID> updates = new ArrayList<>();

            packet.users.forEach((uuid, info) -> {
                connection.base.sessionManager.users.computeIfAbsent(
                    uuid,
                    DataOtherSessionInfo::new
                ).update(info);
                updates.add(uuid);
            });

            if (packet.overrideAll) {
                connection.base.sessionManager.users.forEach((uuid, info) -> {
                    if (!updates.contains(uuid)) {
                        info.markInvalid();
                    }
                });
                connection.base.sessionManager.clearInvalidUsers();
            }
        } else if (serverPacket instanceof S2CPacketDisconnect) {
            final S2CPacketDisconnect packet = (S2CPacketDisconnect) serverPacket;

            connection.base.resultManager.disconnectType = EnumDisconnectType.KICK_BY_SERVER;
            connection.base.resultManager.disconnectReason = packet.reason;
            connection.base.resultManager.disconnectLogout = packet.logout;

            connection.channel.close();
        }

        super.channelRead(ctx, data);
    }
}
