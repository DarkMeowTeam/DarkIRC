package net.darkmeow.irc.data;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class UserInfoData {
    
    public boolean online;

    @NotNull
    public String rank;

    /**
     * 游戏内信息
     * 如果为空则表示玩家离线
     */
    @Nullable
    public GameInfoData info;

    public UserInfoData(boolean online, @NotNull String rank, @Nullable GameInfoData info) {
        this.online = online;
        this.rank = rank;
        this.info = info;
    }

    public UserInfoData(@NotNull String rank, @Nullable GameInfoData info) {
        this.online = true;
        this.rank = rank;
        this.info = info;
    }
}
