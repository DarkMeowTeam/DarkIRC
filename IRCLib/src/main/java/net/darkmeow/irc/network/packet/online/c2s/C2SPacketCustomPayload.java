package net.darkmeow.irc.network.packet.online.c2s;

import lombok.Getter;
import net.darkmeow.irc.network.FriendBuffer;
import net.darkmeow.irc.network.packet.C2SPacket;
import org.jetbrains.annotations.NotNull;

public class C2SPacketCustomPayload implements C2SPacket {

    @Getter
    @NotNull
    private final String channel;

    @Getter
    @NotNull
    private final FriendBuffer data;

    public C2SPacketCustomPayload(@NotNull String channel, @NotNull FriendBuffer data) {
        this.channel = channel;
        this.data = data;
    }

    public C2SPacketCustomPayload(@NotNull FriendBuffer buffer) {
        this.channel = buffer.readString(100);
        this.data = new FriendBuffer(buffer.readBytes(buffer.readableBytes()));
    }

    @Override
    public void write(@NotNull FriendBuffer buffer) {
        buffer.writeString(this.channel);
        synchronized(this.data) {
            this.data.markReaderIndex();
            buffer.writeBytes(this.data);
            this.data.resetReaderIndex();
        }
    }
}
