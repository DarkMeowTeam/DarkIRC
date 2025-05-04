package net.darkmeow.irc.network.packet.online.s2c;

import lombok.Getter;
import net.darkmeow.irc.network.FriendBuffer;
import net.darkmeow.irc.network.packet.S2CPacket;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

public class S2CPacketOtherInputState implements S2CPacket {

    /**
     * 输入状态 - 即将发送公开消息
     */
    @Getter
    @NotNull
    private final Set<UUID> publicInputs;

    /**
     * 输入状态 - 即将对你发送私聊消息
     */
    @Getter
    @NotNull
    private final Set<UUID> privateInputs;

    public S2CPacketOtherInputState(@NotNull Set<UUID> publicInputs, @NotNull Set<UUID> privateInputs) {
        this.publicInputs = publicInputs;
        this.privateInputs = privateInputs;
    }

    public S2CPacketOtherInputState(@NotNull FriendBuffer buffer) {
        final int publicInputSize = buffer.readInt();
        this.publicInputs = new LinkedHashSet<>(publicInputSize);
        for (int i = 0; i < publicInputSize; i++) {

            this.publicInputs.add(buffer.readUniqueId());
        }

        final int privateInputSize = buffer.readInt();
        this.privateInputs = new LinkedHashSet<>(privateInputSize);
        for (int i = 0; i < privateInputSize; i++) {
            this.privateInputs.add(buffer.readUniqueId());
        }
    }

    @Override
    public void write(@NotNull FriendBuffer buffer) {
        buffer.writeInt(publicInputs.size());
        for (UUID uuid : publicInputs) {
            buffer.writeUniqueId(uuid);
        }
        buffer.writeInt(privateInputs.size());
        for (UUID uuid : privateInputs) {
            buffer.writeUniqueId(uuid);
        }
    }
}
