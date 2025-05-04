package net.darkmeow.irc.network.packet.online.c2s;

import lombok.Getter;
import net.darkmeow.irc.data.DataUserState;
import net.darkmeow.irc.network.FriendBuffer;
import net.darkmeow.irc.network.packet.C2SPacket;
import org.jetbrains.annotations.NotNull;

public class C2SPacketUploadState implements C2SPacket {

    @Getter
    private final DataUserState state;

    public C2SPacketUploadState(DataUserState state) {
        this.state = state;
    }

    public C2SPacketUploadState(@NotNull FriendBuffer buffer) {
        this.state = buffer.readUserState();
    }

    @Override
    public void write(@NotNull FriendBuffer buffer) {
        buffer.writeUserState(state);
    }
}
