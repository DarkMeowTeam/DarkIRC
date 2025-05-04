package net.darkmeow.irc.client.interfaces.data;

import net.darkmeow.irc.data.enmus.EnumUserPremium;
import org.jetbrains.annotations.NotNull;

public interface IRCDataSelfSessionInfo extends IRCDataSessionInfo {
    /**
     * 获取当前自身用户名
     *
     * @return 用户名
     */
    @NotNull String getName();
    /**
     * 获取当前自身权限等级
     *
     * @return 权限等级
     */
    @NotNull EnumUserPremium getPremium();
    /**
     * 获取自身是否开启了隐身模式
     *
     * @return 隐身登录状态
     */
    boolean getIsInvisible();
}
