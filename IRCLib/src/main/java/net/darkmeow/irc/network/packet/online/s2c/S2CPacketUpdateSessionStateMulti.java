package net.darkmeow.irc.network.packet.online.s2c;

import lombok.Getter;
import net.darkmeow.irc.data.DataUser;
import net.darkmeow.irc.network.FriendBuffer;
import net.darkmeow.irc.network.packet.S2CPacket;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class S2CPacketUpdateSessionStateMulti implements S2CPacket {

    /**
     * 是否启用仅同服务器玩家可见
     */
    @Getter
    private final boolean onlySameServer;

    /**
     * 该数据为当前服务端全部在线数据
     * 客户端处理该包前会清空客户端存储的旧数据
     */
    @Getter
    private final boolean overrideAll;

    /**
     * 用户数据
     * HashMap<客户端唯一标识, 客户端数据>
     */
    @Getter
    @NotNull
    private final Map<UUID, DataUser> userMap;

    public S2CPacketUpdateSessionStateMulti(boolean onlySameServer, boolean overrideAll, @NotNull Map<UUID, DataUser> userMap) {
        this.onlySameServer = onlySameServer;
        this.overrideAll = overrideAll;
        this.userMap = userMap;
    }

    public S2CPacketUpdateSessionStateMulti(@NotNull FriendBuffer buffer) {
        this.onlySameServer = buffer.readBoolean();
        this.overrideAll = buffer.readBoolean();

        final int size = buffer.readInt();
        this.userMap = new HashMap<>(size);
        for (int i = 0; i < size; i++) {
            this.userMap.put(buffer.readUniqueId(), buffer.readUser());
        }
    }

    @Override
    public void write(@NotNull FriendBuffer buffer) {
        buffer.writeBoolean(this.onlySameServer);
        buffer.writeBoolean(this.overrideAll);

        buffer.writeInt(this.userMap.size());
        for (Map.Entry<UUID, DataUser> entry : this.userMap.entrySet()) {
            buffer.writeUniqueId(entry.getKey());
            buffer.writeUser(entry.getValue());
        }
    }
}
