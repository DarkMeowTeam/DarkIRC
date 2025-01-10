package net.darkmeow.irc.client.interfaces.data;

import net.darkmeow.irc.data.UserInfoData;
import org.jetbrains.annotations.NotNull;

public interface IRCDataOtherSessionInfo extends IRCDataSessionInfo {
    /**
     * 获取会话信息
     *
     * @return 数据
     */
    @NotNull UserInfoData getInfo();
}
