package net.darkmeow.irc.data;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class UserInfoData {

    public static UserInfoData EMPTY = new UserInfoData("", "" , GameInfoData.EMPTY);

    /**
     * IRC 用户名
     */
    @NotNull
    public String name;

    /**
     * IRC 头衔
     */
    @NotNull
    public String rank;

    /**
     * IRC 客户端游戏内信息/参数
     */
    @NotNull
    public GameInfoData info;

    public UserInfoData(@NotNull String name, @NotNull String rank, @NotNull GameInfoData info) {
        this.name = name;
        this.rank = rank;
        this.info = info;
    }
}
