package net.darkmeow.irc.network.packet.online.c2s;

import lombok.Getter;
import net.darkmeow.irc.network.FriendBuffer;
import net.darkmeow.irc.network.packet.C2SPacket;
import org.jetbrains.annotations.NotNull;

public class C2SPacketLogout implements C2SPacket {

    @Getter
    private final boolean destroySessionKey;


    public C2SPacketLogout(boolean destroySessionKey) {
        this.destroySessionKey = destroySessionKey;
    }

    public C2SPacketLogout(@NotNull FriendBuffer buffer) {
        this.destroySessionKey = buffer.readBoolean();
    }

    @Override
    public void write(@NotNull FriendBuffer buffer) {
        buffer.writeBoolean(this.destroySessionKey);
    }
}
