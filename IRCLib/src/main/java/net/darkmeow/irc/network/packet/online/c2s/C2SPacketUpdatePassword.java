package net.darkmeow.irc.network.packet.online.c2s;

import lombok.Getter;
import net.darkmeow.irc.network.FriendBuffer;
import net.darkmeow.irc.network.packet.C2SPacket;
import org.jetbrains.annotations.NotNull;

public class C2SPacketUpdatePassword implements C2SPacket {

    @Getter
    @NotNull
    private final String password;

    public C2SPacketUpdatePassword(@NotNull String password) {
        this.password = password;
    }

    public C2SPacketUpdatePassword(@NotNull FriendBuffer buffer) {
        this.password = buffer.readString(32767);
    }

    @Override
    public void write(@NotNull FriendBuffer buffer) {
        buffer.writeString(this.password);
    }
}
