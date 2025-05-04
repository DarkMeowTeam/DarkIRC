package net.darkmeow.irc.network.packet.online.s2c;

import lombok.Getter;
import net.darkmeow.irc.network.FriendBuffer;
import net.darkmeow.irc.network.packet.S2CPacket;
import org.jetbrains.annotations.NotNull;


public class S2CPacketKeepAlive implements S2CPacket {

    @Getter
    private final long id;

    public S2CPacketKeepAlive(long id) {
        this.id = id;
    }

    public S2CPacketKeepAlive(@NotNull FriendBuffer buffer) {
        this.id = buffer.readLong();
    }

    @Override
    public void write(@NotNull FriendBuffer buffer) {
        buffer.writeLong(this.id);
    }
}
