package net.darkmeow.irc.client.network.handle;

import com.google.gson.JsonParser;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.darkmeow.irc.client.data.IRCResultSendMessageToPrivate;
import net.darkmeow.irc.client.data.IRCUserInfo;
import net.darkmeow.irc.client.enums.EnumDisconnectType;
import net.darkmeow.irc.client.enums.EnumPremium;
import net.darkmeow.irc.client.enums.EnumResultLogin;
import net.darkmeow.irc.client.network.IRCClientConnection;
import net.darkmeow.irc.network.PacketUtils;
import net.darkmeow.irc.network.packet.c2s.C2SPacketKeepAlive;
import net.darkmeow.irc.network.packet.c2s.C2SPacketQueryUsers;
import net.darkmeow.irc.network.packet.s2c.*;

public class HandleClientPacketProcess extends ChannelInboundHandlerAdapter {

    public final IRCClientConnection connection;

    public HandleClientPacketProcess(IRCClientConnection connection) {
        this.connection = connection;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object data) throws Exception {
        final S2CPacket serverPacket = PacketUtils.resolveServerPacket(JsonParser.parseString(data.toString()).getAsJsonObject());

        if (serverPacket instanceof S2CPacketHandShake) {
            // 握手包 客户端主动发 服务端回应
            connection.base.resultManager.handShakeLatch.countDown();
        } else if (serverPacket instanceof S2CPacketKeepAlive) {
            // 心跳包 服务端发送 客户端回应
            connection.sendPacket(new C2SPacketKeepAlive(((S2CPacketKeepAlive) serverPacket).id), true);
        } else if (serverPacket instanceof S2CPacketLoginResult) {
            if (connection.base.resultManager.loginResultCallback != null) {
                final S2CPacketLoginResult.LoginResult result = ((S2CPacketLoginResult) serverPacket).result;
                if (result == S2CPacketLoginResult.LoginResult.INVALID_CLIENT) {
                    connection.base.resultManager.loginResultCallback.accept(EnumResultLogin.INVALID_CLIENT);
                } else if (result == S2CPacketLoginResult.LoginResult.OUTDATED_CLIENT_VERSION) {
                    connection.base.resultManager.loginResultCallback.accept(EnumResultLogin.OUTDATED_CLIENT_VERSION);
                } else if (result == S2CPacketLoginResult.LoginResult.USER_OR_PASSWORD_WRONG) {
                    connection.base.resultManager.loginResultCallback.accept(EnumResultLogin.USER_OR_PASSWORD_WRONG);
                } else if (result == S2CPacketLoginResult.LoginResult.SUCCESS) {
                    connection.base.resultManager.loginResultCallback.accept(EnumResultLogin.SUCCESS);
                    connection.sendPacket(new C2SPacketQueryUsers(false), true);
                }
            }
        } else if (serverPacket instanceof S2CPacketUpdateMyInfo) {
            connection.base.name = ((S2CPacketUpdateMyInfo) serverPacket).name;
            connection.base.rank = ((S2CPacketUpdateMyInfo) serverPacket).rank;
            connection.base.premium =  EnumPremium.getEnumPremiumFromPacket(((S2CPacketUpdateMyInfo) serverPacket).premium);

            connection.base.listenable.onUpdateUserInfo(connection.base.name, connection.base.rank, connection.base.premium);
        } else if (serverPacket instanceof S2CPacketMessagePublic) {
            final S2CPacketMessagePublic packet = (S2CPacketMessagePublic) serverPacket;

            final IRCUserInfo info = connection.base.userManager.users.computeIfAbsent(
                packet.sessionUniqueId,
                IRCUserInfo::new
            );
            info.update(packet.info);

            connection.base.listenable.onMessagePublic(info, packet.message);
        } else if (serverPacket instanceof S2CPacketMessagePrivate) {
            final S2CPacketMessagePrivate packet = (S2CPacketMessagePrivate) serverPacket;

            final IRCUserInfo info = connection.base.userManager.users.computeIfAbsent(
                packet.sessionUniqueId,
                IRCUserInfo::new
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
        } else if (serverPacket instanceof S2CPacketUpdateOtherInfo) {
            final S2CPacketUpdateOtherInfo packet = (S2CPacketUpdateOtherInfo) serverPacket;

            if (packet.info == null) {
                connection.base.userManager.users.remove(((S2CPacketUpdateOtherInfo) serverPacket).sessionUniqueId);
            } else {
                connection.base.userManager.users.computeIfAbsent(
                    ((S2CPacketUpdateOtherInfo) serverPacket).sessionUniqueId,
                    IRCUserInfo::new
                ).update(packet.info);
            }
        } else if (serverPacket instanceof S2CPacketUpdateMultiUserInfo) {
            final S2CPacketUpdateMultiUserInfo packet = (S2CPacketUpdateMultiUserInfo) serverPacket;

            if (packet.overrideAll) {
                connection.base.userManager.users.clear();
            }

            packet.users.forEach((uuid, info) ->
                connection.base.userManager.users.computeIfAbsent(
                    uuid,
                    IRCUserInfo::new
                ).update(info)
            );
        } else if (serverPacket instanceof S2CPacketDisconnect) {
            final S2CPacketDisconnect packet = (S2CPacketDisconnect) serverPacket;
            connection.base.resultManager.disconnectType = EnumDisconnectType.KICK_BY_SERVER;
            connection.base.resultManager.disconnectReason = packet.reason;
            connection.base.resultManager.disconnectLogout = packet.logout;
            connection.disconnect();
        }

        super.channelRead(ctx, data);
    }
}
