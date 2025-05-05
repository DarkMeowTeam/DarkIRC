package net.darkmeow.irc.network;

import net.darkmeow.irc.network.packet.Packet;
import net.darkmeow.irc.network.packet.handshake.c2s.C2SPacketEncryptionResponse;
import net.darkmeow.irc.network.packet.handshake.c2s.C2SPacketHandShake;
import net.darkmeow.irc.network.packet.handshake.c2s.C2SPacketSignatureResponse;
import net.darkmeow.irc.network.packet.handshake.s2c.*;
import net.darkmeow.irc.network.packet.login.c2s.C2SPacketLogin;
import net.darkmeow.irc.network.packet.login.s2c.S2CPacketLoginFailed;
import net.darkmeow.irc.network.packet.login.s2c.S2CPacketLoginSuccess;
import net.darkmeow.irc.network.packet.online.c2s.*;
import net.darkmeow.irc.network.packet.online.s2c.*;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumMap;

public enum EnumConnectionState {
    HANDSHAKING(0)
        {
            {
                // 基本握手包
                this.registerPacket(EnumPacketDirection.SERVER_BOUND, C2SPacketHandShake.class);
                // 加密和来源验证
                this.registerPacket(EnumPacketDirection.SERVER_BOUND, C2SPacketEncryptionResponse.class);
                this.registerPacket(EnumPacketDirection.SERVER_BOUND, C2SPacketSignatureResponse.class);

                // 基本握手回应
                this.registerPacket(EnumPacketDirection.CLIENT_BOUND, S2CPacketHandShakeSuccess.class);
                // 加密和来源验证
                this.registerPacket(EnumPacketDirection.CLIENT_BOUND, S2CPacketDenyHandShake.class);
                this.registerPacket(EnumPacketDirection.CLIENT_BOUND, S2CPacketEncryptionRequest.class);
                // 拒绝登录
                this.registerPacket(EnumPacketDirection.CLIENT_BOUND, S2CPacketSignatureRequest.class);
                // 重定向
                this.registerPacket(EnumPacketDirection.CLIENT_BOUND, S2CPacketRedirectServer.class);
            }
        },
    LOGIN(1)
        {
            {
                this.registerPacket(EnumPacketDirection.SERVER_BOUND, C2SPacketLogin.class);

                this.registerPacket(EnumPacketDirection.CLIENT_BOUND, S2CPacketLoginFailed.class);
                this.registerPacket(EnumPacketDirection.CLIENT_BOUND, S2CPacketLoginSuccess.class);
            }
        },
    ONLINE(2)
        {
            {
                // 心跳包
                this.registerPacket(EnumPacketDirection.SERVER_BOUND, C2SPacketKeepAlive.class);
                // 基础消息
                this.registerPacket(EnumPacketDirection.SERVER_BOUND, C2SPacketMessage.class);
                this.registerPacket(EnumPacketDirection.SERVER_BOUND, C2SPacketInputStatus.class);
                // 游戏内状态
                this.registerPacket(EnumPacketDirection.SERVER_BOUND, C2SPacketUploadState.class);
                this.registerPacket(EnumPacketDirection.SERVER_BOUND, C2SPacketQuerySessions.class);
                this.registerPacket(EnumPacketDirection.SERVER_BOUND, C2SPacketUploadSkin.class);
                this.registerPacket(EnumPacketDirection.SERVER_BOUND, C2SPacketQuerySkin.class);
                // 会话状态
                this.registerPacket(EnumPacketDirection.SERVER_BOUND, C2SPacketLogout.class);
                this.registerPacket(EnumPacketDirection.SERVER_BOUND, C2SPacketUpdatePassword.class);
                // 自定义包
                this.registerPacket(EnumPacketDirection.SERVER_BOUND, C2SPacketCustomPayload.class);

                // 心跳包
                this.registerPacket(EnumPacketDirection.CLIENT_BOUND, S2CPacketKeepAlive.class);
                // 基础消息
                this.registerPacket(EnumPacketDirection.CLIENT_BOUND, S2CPacketSessionMessage.class);
                this.registerPacket(EnumPacketDirection.CLIENT_BOUND, S2CPacketSystemMessage.class);
                this.registerPacket(EnumPacketDirection.CLIENT_BOUND, S2CPacketPrivateMessageResult.class);
                this.registerPacket(EnumPacketDirection.CLIENT_BOUND, S2CPacketOtherInputState.class);
                // 游戏内状态
                this.registerPacket(EnumPacketDirection.CLIENT_BOUND, S2CPacketUpdateMyProfile.class);
                this.registerPacket(EnumPacketDirection.CLIENT_BOUND, S2CPacketUpdateSessionState.class);
                this.registerPacket(EnumPacketDirection.CLIENT_BOUND, S2CPacketUpdateSessionStateMulti.class);
                this.registerPacket(EnumPacketDirection.CLIENT_BOUND, S2CPacketUpdateSkin.class);
                // 会话状态
                this.registerPacket(EnumPacketDirection.CLIENT_BOUND, S2CPacketDisconnect.class);
                // 自定义包
                this.registerPacket(EnumPacketDirection.CLIENT_BOUND, S2CPacketCustomPayload.class);
            }
        };

    private final int id;
    private final EnumMap<EnumPacketDirection, ArrayList<Class<? extends Packet>>> directionMaps = new EnumMap<>(EnumPacketDirection.class);

    EnumConnectionState(int protocolId) {
        this.id = protocolId;
    }

    protected void registerPacket(EnumPacketDirection direction, Class<? extends Packet> packetClass) {
        ArrayList<Class<? extends Packet>> directionMap = this.directionMaps.computeIfAbsent(direction, k -> new ArrayList<>());

        if (directionMap.contains(packetClass)) {
            throw new IllegalArgumentException("Packet class " + packetClass + " is already registered.");
        } else {
            directionMap.add(packetClass);
        }
    }

    public int getPacketId(EnumPacketDirection direction, Packet packetIn) throws IllegalArgumentException {
        int id = this.directionMaps.get(direction).indexOf(packetIn.getClass());
        if (id < 0) {
            throw new IllegalArgumentException("Unknown packet " + packetIn.getClass() + ".");
        }
        return id;
    }

    @Nullable
    public Class<? extends Packet> getPacketClassById(EnumPacketDirection direction, int packetId)
    {
        try {
            return this.directionMaps.get(direction).get(packetId);
        } catch (Exception e) {
            return null;
        }
    }

    @Nullable
    public Packet newPacketClassById(EnumPacketDirection direction, int packetId, FriendBuffer buffer) throws Exception {
        final Class<? extends Packet> clazz = this.getPacketClassById(direction, packetId);
        return clazz == null ? null : clazz.getConstructor(FriendBuffer.class).newInstance(buffer);
    }

}
