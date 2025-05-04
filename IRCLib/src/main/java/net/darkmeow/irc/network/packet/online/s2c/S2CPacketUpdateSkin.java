package net.darkmeow.irc.network.packet.online.s2c;

import lombok.Getter;
import net.darkmeow.irc.data.DataSkin;
import net.darkmeow.irc.network.FriendBuffer;
import net.darkmeow.irc.network.packet.S2CPacket;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class S2CPacketUpdateSkin implements S2CPacket {

    @Getter
    private final UUID sessionId;

    @Getter
    @Nullable
    private final DataSkin skin;

    /**
     * 回应客户端查询用户皮肤数据
     *
     * @param sessionId 会话标识
     * @param skin 皮肤信息
     */
    public S2CPacketUpdateSkin(UUID sessionId, @NotNull DataSkin skin) {
        this.sessionId = sessionId;
        this.skin = skin;
    }

    /**
     * 标记用户已更换皮肤
     *
     * @param sessionId 会话标识
     */
    public S2CPacketUpdateSkin(UUID sessionId) {
        this.sessionId = sessionId;
        this.skin = null;
    }

    public S2CPacketUpdateSkin(@NotNull FriendBuffer buffer) {
        this.sessionId = buffer.readUniqueId();
        this.skin = buffer.readBoolean() ? buffer.readSkin() : null;
    }

    @Override
    public void write(@NotNull FriendBuffer buffer) {
        buffer.writeUniqueId(this.sessionId);
        if (skin != null) {
            buffer.writeBoolean(true);
            buffer.writeSkin(this.skin);
        } else {
            buffer.writeBoolean(false);
        }
    }
}
