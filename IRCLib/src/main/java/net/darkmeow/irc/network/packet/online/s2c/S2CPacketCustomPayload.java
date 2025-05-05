package net.darkmeow.irc.network.packet.online.s2c;

import lombok.Getter;
import net.darkmeow.irc.network.FriendBuffer;
import net.darkmeow.irc.network.packet.S2CPacket;
import org.jetbrains.annotations.NotNull;

public class S2CPacketCustomPayload implements S2CPacket {

    @Getter
    @NotNull
    private final String channel;

    @Getter
    @NotNull
    private final FriendBuffer data;

    public S2CPacketCustomPayload(@NotNull String channel, @NotNull FriendBuffer data) {
        this.channel = channel;
        this.data = data;
    }

    public S2CPacketCustomPayload(@NotNull FriendBuffer buffer) {
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
