package net.darkmeow.irc.client.interfaces.data;

import net.darkmeow.irc.data.DataSkin;
import net.darkmeow.irc.data.DataUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IRCDataOtherSessionInfo extends IRCDataSessionInfo {
    /**
     * 获取会话信息
     *
     * @return 数据
     */
    @NotNull DataUser getInfo();
    /**
     * 获取会话绑定皮肤信息
     *
     * @return 皮肤信息 可能为空
     */
    @Nullable DataSkin getSkin();
}
