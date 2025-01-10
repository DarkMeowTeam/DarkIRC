package net.darkmeow.irc.data;

import org.jetbrains.annotations.NotNull;

public class UserInfoData {

    public static UserInfoData EMPTY = new UserInfoData("", "" , DataSessionInfo.EMPTY, DataSessionOptions.EMPTY);

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
     * 会话信息
     * 由服务端生成
     */
    @NotNull
    public DataSessionInfo info;

    /**
     * 会话自定义信息
     * 由其它客户端生成
     */
    @NotNull
    public DataSessionOptions options;

    public UserInfoData(@NotNull String name, @NotNull String rank, @NotNull DataSessionInfo info, @NotNull DataSessionOptions options) {
        this.name = name;
        this.rank = rank;
        this.info = info;
        this.options = options;
    }
}
