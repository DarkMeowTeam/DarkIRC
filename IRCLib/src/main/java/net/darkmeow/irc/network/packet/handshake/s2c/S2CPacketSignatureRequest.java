package net.darkmeow.irc.network.packet.handshake.s2c;

import lombok.Getter;
import net.darkmeow.irc.network.FriendBuffer;
import net.darkmeow.irc.network.packet.S2CPacket;
import org.jetbrains.annotations.NotNull;

/**
 * 客户端合法性验证包
 * 收到后需要用存储的私钥进行签名
 */
public class S2CPacketSignatureRequest implements S2CPacket {

    @Getter
    @NotNull
    private final String code;

    public S2CPacketSignatureRequest(@NotNull String code) {
        this.code = code;
    }

    public S2CPacketSignatureRequest(@NotNull FriendBuffer buffer) {
        this.code = buffer.readString(32767);
    }

    @Override
    public void write(@NotNull FriendBuffer buffer) {
        buffer.writeString(code);
    }

}
