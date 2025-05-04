package net.darkmeow.irc.network.packet.handshake.s2c;

import lombok.Getter;
import net.darkmeow.irc.network.FriendBuffer;
import net.darkmeow.irc.network.packet.S2CPacket;
import org.jetbrains.annotations.NotNull;

/**
 * 服务端拒绝握手
 */
public class S2CPacketDenyHandShake implements S2CPacket {

    @Getter
    @NotNull
    private final String reason;

    public S2CPacketDenyHandShake(@NotNull String reason) {
        this.reason = reason;
    }

    public S2CPacketDenyHandShake(@NotNull FriendBuffer buffer) {
        this.reason = buffer.readString(32767);
    }

    @Override
    public void write(@NotNull FriendBuffer buffer) {
        buffer.writeString(reason);
    }
}
