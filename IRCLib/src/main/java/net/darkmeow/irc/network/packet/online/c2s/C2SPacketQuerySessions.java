package net.darkmeow.irc.network.packet.online.c2s;

import lombok.Getter;
import net.darkmeow.irc.network.FriendBuffer;
import net.darkmeow.irc.network.packet.C2SPacket;
import org.jetbrains.annotations.NotNull;

public class C2SPacketQuerySessions implements C2SPacket {

    @Getter
    private final boolean onlySameServer;

    public C2SPacketQuerySessions(boolean onlySameServer) {
        this.onlySameServer = onlySameServer;
    }

    public C2SPacketQuerySessions(@NotNull FriendBuffer buffer) {
        this.onlySameServer = buffer.readBoolean();
    }

    @Override
    public void write(@NotNull FriendBuffer buffer) {
        buffer.writeBoolean(this.onlySameServer);
    }
}
