package net.darkmeow.irc.network.packet.handshake.s2c;

import lombok.Getter;
import net.darkmeow.irc.network.FriendBuffer;
import net.darkmeow.irc.network.packet.S2CPacket;
import org.jetbrains.annotations.NotNull;

public class S2CPacketEnableCompression implements S2CPacket {

    @Getter
    private final int threshold;

    public S2CPacketEnableCompression(int threshold) {
        this.threshold = threshold;
    }

    public S2CPacketEnableCompression(@NotNull FriendBuffer buffer) {
        this.threshold = buffer.readInt();
    }

    @Override
    public void write(@NotNull FriendBuffer buffer) {
        buffer.writeInt(this.threshold);
    }

}
