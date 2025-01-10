package net.darkmeow.irc.network.packet.s2c;

import net.darkmeow.irc.data.UserInfoData;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

public class S2CPacketUpdateMultiSessionInfo implements S2CPacket {

    /**
     * 是否启用仅同服务器玩家可见
     */
    public boolean onlySameServer;

    /**
     * 该数据为当前服务端全部在线数据
     * 客户端处理该包前会清空客户端存储的旧数据
     */
    public boolean overrideAll;

    /**
     * 用户数据
     * HashMap<客户端唯一标识, 客户端数据>
     */
    @NotNull
    public HashMap<UUID, UserInfoData> users;

    public S2CPacketUpdateMultiSessionInfo(boolean onlySameServer, boolean overrideAll, @NotNull HashMap<UUID, UserInfoData> users) {
        this.onlySameServer = onlySameServer;
        this.overrideAll = overrideAll;
        this.users = users;
    }

}
