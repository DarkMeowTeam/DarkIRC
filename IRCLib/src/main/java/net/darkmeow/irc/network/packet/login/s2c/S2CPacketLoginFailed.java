package net.darkmeow.irc.network.packet.login.s2c;

import lombok.Getter;
import net.darkmeow.irc.network.FriendBuffer;
import net.darkmeow.irc.network.packet.S2CPacket;
import org.jetbrains.annotations.NotNull;

public class S2CPacketLoginFailed implements S2CPacket {

    @Getter
    @NotNull
    private final String reason;

    public S2CPacketLoginFailed(@NotNull String reason) {
        this.reason = reason;
    }

    public S2CPacketLoginFailed(@NotNull FriendBuffer buffer) {
        this.reason = buffer.readString(32767);
    }

    @Override
    public void write(@NotNull FriendBuffer buffer) {
        buffer.writeString(reason);
    }
}
