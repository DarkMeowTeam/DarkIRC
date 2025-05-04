package net.darkmeow.irc.network.packet.online.s2c;

import lombok.Getter;
import net.darkmeow.irc.data.DataUser;
import net.darkmeow.irc.network.FriendBuffer;
import net.darkmeow.irc.network.packet.C2SPacket;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class S2CPacketUpdateSessionState implements C2SPacket {

    @Getter
    @NotNull
    private final UUID id;

    @Getter
    @Nullable
    private final DataUser user;

    /**
     * 诉客户端更新/创建用户信息
     *
     * @param id 唯一标识
     * @param user 信息
     */
    public S2CPacketUpdateSessionState(@NotNull UUID id, @NotNull DataUser user) {
        this.id = id;
        this.user = user;
    }

    /**
     * 告诉客户端删除用户信息
     *
     * @param id 唯一标识
     */
    public S2CPacketUpdateSessionState(@NotNull UUID id) {
        this.id = id;
        this.user = null;
    }

    public S2CPacketUpdateSessionState(@NotNull FriendBuffer buffer) {
        this.id = buffer.readUniqueId();
        if (buffer.readBoolean()) {
            this.user = buffer.readUser();
        } else {
            this.user = null;
        }
    }

    @Override
    public void write(@NotNull FriendBuffer buffer) {
        buffer.writeUniqueId(this.id);
        if (this.user != null) {
            buffer.writeBoolean(true);
            buffer.writeUser(this.user);
        } else {
            buffer.writeBoolean(false);
        }
    }
}
