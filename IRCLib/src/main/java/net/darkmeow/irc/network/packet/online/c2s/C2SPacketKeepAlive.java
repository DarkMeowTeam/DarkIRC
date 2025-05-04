package net.darkmeow.irc.network.packet.online.c2s;

import lombok.Getter;
import net.darkmeow.irc.network.FriendBuffer;
import net.darkmeow.irc.network.packet.C2SPacket;
import org.jetbrains.annotations.NotNull;

public class C2SPacketKeepAlive implements C2SPacket {

    @Getter
    private final long id;

    public C2SPacketKeepAlive(long id) {
        this.id = id;
    }

    public C2SPacketKeepAlive(@NotNull FriendBuffer buffer) {
        this.id = buffer.readLong();
    }

    @Override
    public void write(@NotNull FriendBuffer buffer) {
        buffer.writeLong(this.id);
    }
}
