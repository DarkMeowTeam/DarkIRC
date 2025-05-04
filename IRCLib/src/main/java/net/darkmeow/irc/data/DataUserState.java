package net.darkmeow.irc.data;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class DataUserState {

    /**
     * 当前正在游玩的服务器
     * 正在游玩单人世界
     */
    public static final String SERVER_SINGLE_PLAY = "SinglePlay";
    /**
     * 当前正在游玩的服务器
     * 未连接任何服务器 / 处于 SilentDisconnect 状态
     */
    public static final String SERVER_DISCONNECTED = "Disconnected";

    public static final DataUserState EMPTY = new DataUserState(new DataPlayInGameProfile("", new UUID(0L, 0L)), SERVER_DISCONNECTED, 0, false);

    @Getter
    @NotNull
    private final DataPlayInGameProfile profile;

    /**
     * 当前正在游玩的服务器
     */
    @Getter
    @NotNull
    private final String currentServer;

    /**
     * 客户端帧率
     */
    @Getter
    private final int clientFps;

    /**
     * 客户端配置了不攻击同 IRC 内其它用户
     */
    @Getter
    private final boolean friend;

    public DataUserState(@NotNull DataPlayInGameProfile profile, @NotNull String currentServer, int clientFps, boolean friend) {
        this.profile = profile;
        this.currentServer = currentServer;
        this.clientFps = clientFps;
        this.friend = friend;
    }
}
