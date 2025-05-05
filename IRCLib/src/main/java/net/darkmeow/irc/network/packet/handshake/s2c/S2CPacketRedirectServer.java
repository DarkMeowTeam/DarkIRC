package net.darkmeow.irc.network.packet.handshake.s2c;

import lombok.Getter;
import net.darkmeow.irc.network.FriendBuffer;
import net.darkmeow.irc.network.packet.S2CPacket;
import org.jetbrains.annotations.NotNull;

public class S2CPacketRedirectServer implements S2CPacket {

    @Getter
    @NotNull
    private final String host;

    @Getter
    private final int port;

    public S2CPacketRedirectServer(@NotNull String host, int port) {
        this.host = host;
        this.port = port;
    }

    public S2CPacketRedirectServer(@NotNull FriendBuffer buffer) {
        this.host = buffer.readString(32767);
        this.port = buffer.readInt();
    }

    @Override
    public void write(@NotNull FriendBuffer buffer) {
        buffer.writeString(this.host);
        buffer.writeInt(this.port);
    }

}
