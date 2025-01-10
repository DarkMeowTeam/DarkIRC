package net.darkmeow.irc.client.interfaces.data;

import net.darkmeow.irc.client.enums.EnumPremium;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface IRCDataSelfInfo {
    /**
     * 数据是否有效
     * 当客户端断开连接后数据将会无效
     *
     * @return 数据是否有效
     */
    boolean isInvalid();
    /**
     * 获取自身客户端连接唯一ID
     * 该唯一ID会在重新连接后变动
     *
     * @return 唯一ID
     */
    @NotNull UUID getUniqueId();
    /**
     * 获取当前自身用户名
     *
     * @return 用户名
     */
    @NotNull String getName();
    /**
     * 获取当前自身头衔
     *
     * @return 头衔
     */
    @NotNull String getRank();
    /**
     * 获取当前自身权限等级
     *
     * @return 权限等级
     */
    @NotNull EnumPremium getPremium();
}
