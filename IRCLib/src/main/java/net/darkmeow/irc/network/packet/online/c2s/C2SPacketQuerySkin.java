package net.darkmeow.irc.network.packet.online.c2s;

import lombok.Getter;
import net.darkmeow.irc.network.FriendBuffer;
import net.darkmeow.irc.network.packet.C2SPacket;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class C2SPacketQuerySkin implements C2SPacket {

    @Getter
    private final UUID sessionId;

    public C2SPacketQuerySkin(UUID sessionId) {
        this.sessionId = sessionId;
    }

    public C2SPacketQuerySkin(@NotNull FriendBuffer buffer) {
        this.sessionId = buffer.readUniqueId();
    }

    @Override
    public void write(@NotNull FriendBuffer buffer) {
        buffer.writeUniqueId(this.sessionId);
    }
}
