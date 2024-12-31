package net.darkmeow.irc.network.packet.s2c;

import net.darkmeow.irc.data.UserInfoData;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class S2CPacketUpdateMultiUserInfo implements S2CPacket {

    public boolean onlySameServer;

    public boolean overrideAll;
    /**
     * 用户数据
     * HashMap<用户名, Pair<Rank, 游戏内数据>>
     */
    @NotNull
    public HashMap<String, UserInfoData> users;

    public S2CPacketUpdateMultiUserInfo(boolean onlySameServer, boolean overrideAll, @NotNull HashMap<String, UserInfoData> users) {
        this.onlySameServer = onlySameServer;
        this.overrideAll = overrideAll;
        this.users = users;
    }

}
