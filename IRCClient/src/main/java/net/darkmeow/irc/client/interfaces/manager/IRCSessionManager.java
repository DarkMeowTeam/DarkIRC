package net.darkmeow.irc.client.interfaces.manager;

import net.darkmeow.irc.client.interfaces.data.IRCDataOtherSessionInfo;
import net.darkmeow.irc.client.interfaces.data.IRCDataSelfSessionInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public interface IRCSessionManager {
    /**
     * 获取自身会话信息
     *
     * @return 自身会话信息 如果未登录则为空
     */
    @Nullable IRCDataSelfSessionInfo getSelfSession();
    /**
     * 获取在线会话列表
     *
     * @return 一个列表 包含所有在线会话
     */
    @NotNull ConcurrentHashMap<UUID, ? extends IRCDataOtherSessionInfo> getSessions();
}
