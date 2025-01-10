package net.darkmeow.irc.network.packet.s2c;

import net.darkmeow.irc.data.UserInfoData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class S2CPacketUpdateOtherSessionInfo implements S2CPacket {

    @NotNull
    public UUID sessionUniqueId;

    @Nullable
    public UserInfoData info;

    /**
     *
     * @param sessionUniqueId 客户端唯一标识
     * @param info 客户端数据 为空代表删除数据
     */
    public S2CPacketUpdateOtherSessionInfo(@NotNull UUID sessionUniqueId, @Nullable UserInfoData info) {
        this.sessionUniqueId = sessionUniqueId;
        this.info = info;
    }

}
