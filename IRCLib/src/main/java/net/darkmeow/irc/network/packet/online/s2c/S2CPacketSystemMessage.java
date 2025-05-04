package net.darkmeow.irc.network.packet.online.s2c;

import lombok.Getter;
import net.darkmeow.irc.network.FriendBuffer;
import net.darkmeow.irc.network.packet.S2CPacket;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class S2CPacketSystemMessage implements S2CPacket {

    /**
     * 消息内容
     */
    @Getter
    @NotNull
    private final String message;

    /**
     * 消息唯一ID
     */
    @Getter
    @NotNull
    private final UUID id;

    public S2CPacketSystemMessage(@NotNull String message, @NotNull UUID id) {
        this.message = message;
        this.id = id;
    }

    public S2CPacketSystemMessage(@NotNull FriendBuffer buffer) {
        this.message = buffer.readString(32767);
        this.id = buffer.readUniqueId();
    }

    @Override
    public void write(@NotNull FriendBuffer buffer) {
        buffer.writeString(this.message);
        buffer.writeUniqueId(this.id);
    }
}
