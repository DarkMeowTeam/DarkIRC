package net.darkmeow.irc.client.interfaces.data;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface IRCDataSessionInfo {
    /**
     * 数据是否有效
     * 当客户端断开连接后数据将会无效
     *
     * @return 数据是否有效
     */
    boolean isValid();
    /**
     * 获取客户端连接唯一ID
     * 该唯一ID会在自身重新连接/目标用户掉线重新上线后变动
     *
     * @return 唯一ID
     */
    @NotNull UUID getUniqueId();
}
