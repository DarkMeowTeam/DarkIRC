package net.darkmeow.irc.client.data;

import net.darkmeow.irc.data.UserInfoData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class IRCUserInfo {

    public final UUID uniqueId;

    public boolean online;

    public long lastUpdate;

    @Nullable
    public UserInfoData info;

    public IRCUserInfo(UUID clientUniqueId) {
        this.uniqueId = clientUniqueId;
        this.lastUpdate = System.currentTimeMillis();
        this.info = null;
        this.online = false;
    }

    public IRCUserInfo(UUID clientUniqueId, @NotNull UserInfoData info) {
        this.uniqueId = clientUniqueId;
        this.lastUpdate = System.currentTimeMillis();
        this.info = info;
        this.online = true;
    }

    /**
     * 更新 IRCUserInfo 数据 为 离线状态
     */
    public void update() {
        this.lastUpdate = System.currentTimeMillis();
        this.info = null;
        this.online = false;
    }

    /**
     * 更新 IRCUserInfo 数据 为 在线状态
     *
     * @param info 用户数据
     */
    public void update(@NotNull UserInfoData info) {
        this.lastUpdate = System.currentTimeMillis();
        this.info = info;
        this.online = true;
    }


}
