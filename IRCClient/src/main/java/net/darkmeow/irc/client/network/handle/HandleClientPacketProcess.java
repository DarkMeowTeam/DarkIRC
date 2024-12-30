package net.darkmeow.irc.client.network.handle;

import com.google.gson.JsonParser;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import net.darkmeow.irc.client.data.IRCOtherUserInfo;
import net.darkmeow.irc.client.enums.EnumPremium;
import net.darkmeow.irc.client.enums.EnumResultLogin;
import net.darkmeow.irc.client.network.IRCClientConnection;
import net.darkmeow.irc.network.PacketUtils;
import net.darkmeow.irc.network.packet.c2s.C2SPacketKeepAlive;
import net.darkmeow.irc.network.packet.s2c.*;

import java.util.Objects;

public class HandleClientPacketProcess extends ChannelHandlerAdapter {

    public final IRCClientConnection connection;

    public HandleClientPacketProcess(IRCClientConnection connection) {
        this.connection = connection;
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object data, ChannelPromise promise) throws Exception {
        super.write(ctx, data, promise);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object data) throws Exception {
        final S2CPacket packet = PacketUtils.resolveServerPacket(JsonParser.parseString(data.toString()).getAsJsonObject());

        // 心跳包 (服务端发送 客户端回应)
        if (packet instanceof S2CPacketKeepAlive) {
            connection.sendPacket(new C2SPacketKeepAlive(((S2CPacketKeepAlive) packet).id));
        } else if (packet instanceof S2CPacketLoginResult) {
            if (connection.base.resultManager.loginResultCallback != null) {
                final S2CPacketLoginResult.LoginResult result = ((S2CPacketLoginResult) packet).result;
                if (result == S2CPacketLoginResult.LoginResult.INVALID_CLIENT) {
                    connection.base.resultManager.loginResultCallback.invoke(EnumResultLogin.INVALID_CLIENT);
                } else if (result == S2CPacketLoginResult.LoginResult.OUTDATED_CLIENT_VERSION) {
                    connection.base.resultManager.loginResultCallback.invoke(EnumResultLogin.OUTDATED_CLIENT_VERSION);
                } else if (result == S2CPacketLoginResult.LoginResult.USER_OR_PASSWORD_WRONG) {
                    connection.base.resultManager.loginResultCallback.invoke(EnumResultLogin.USER_OR_PASSWORD_WRONG);
                } else if (result == S2CPacketLoginResult.LoginResult.SUCCESS) {
                    connection.base.resultManager.loginResultCallback.invoke(EnumResultLogin.SUCCESS);
                }
            }
        } else if (packet instanceof S2CPacketUpdateMyInfo) {
            connection.base.name = ((S2CPacketUpdateMyInfo) packet).name;
            connection.base.rank = ((S2CPacketUpdateMyInfo) packet).rank;
            connection.base.premium =  EnumPremium.getEnumPremiumFromPacket(((S2CPacketUpdateMyInfo) packet).premium);

            connection.base.listenable.onUpdateUserInfo(connection.base.name, connection.base.rank, connection.base.premium);
        } else if (packet instanceof S2CPacketMessagePublic) {
            final IRCOtherUserInfo info = new IRCOtherUserInfo(
                ((S2CPacketMessagePublic) packet).name,
                ((S2CPacketMessagePublic) packet).rank,
                ((S2CPacketMessagePublic) packet).info
            );
            connection.base.listenable.onMessagePublic(
                info,
                ((S2CPacketMessagePublic) packet).message
            );
            connection.base.userManager.users.put(((S2CPacketMessagePublic) packet).name, info);
        } else if (packet instanceof S2CPacketMessagePrivate) {
            final IRCOtherUserInfo info = new IRCOtherUserInfo(
                ((S2CPacketMessagePrivate) packet).name,
                ((S2CPacketMessagePrivate) packet).rank,
                ((S2CPacketMessagePrivate) packet).info
            );
            connection.base.listenable.onMessagePublic(
                info,
                ((S2CPacketMessagePrivate) packet).message
            );
            connection.base.userManager.users.put(((S2CPacketMessagePrivate) packet).name, info);
        } else if (packet instanceof S2CPacketMessageSystem) {
            connection.base.listenable.onMessageSystem(((S2CPacketMessageSystem) packet).message);
        } else if (packet instanceof S2CPacketMessagePrivateResult) {
            if (connection.base.resultManager.privateResultCallback != null) {
                connection.base.resultManager.privateResultCallback.invoke(
                    ((S2CPacketMessagePrivateResult) packet).name,
                    ((S2CPacketMessagePrivateResult) packet).message,
                    ((S2CPacketMessagePrivateResult) packet).success
                );
            }
        } else if (packet instanceof S2CPacketUpdateOtherInfo) {
            if (Objects.equals(((S2CPacketUpdateOtherInfo) packet).info.inGameName, "")) {
                connection.base.userManager.users.remove(((S2CPacketUpdateOtherInfo) packet).name);
            } else {
                connection.base.userManager.users.put(((S2CPacketUpdateOtherInfo) packet).name, new IRCOtherUserInfo(
                    ((S2CPacketUpdateOtherInfo) packet).name,
                    ((S2CPacketUpdateOtherInfo) packet).rank,
                    ((S2CPacketUpdateOtherInfo) packet).info
                ));
            }
        }


        System.out.println(data);

        super.channelRead(ctx, data);
    }
}
