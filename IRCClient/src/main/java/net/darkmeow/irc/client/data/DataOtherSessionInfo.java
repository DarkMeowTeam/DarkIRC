package net.darkmeow.irc.client.data;

import net.darkmeow.irc.client.interfaces.data.IRCDataOtherSessionInfo;
import net.darkmeow.irc.data.UserInfoData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public final class DataOtherSessionInfo extends DataSessionInfo implements IRCDataOtherSessionInfo {

    public long lastUpdate;

    @Nullable
    public UserInfoData info;

    public DataOtherSessionInfo(UUID clientUniqueId) {
        super(clientUniqueId);

        this.valid = false;
        this.lastUpdate = System.currentTimeMillis();

        this.info = null;
    }

    /**
     * 更新数据
     *
     * @param info 用户数据
     */
    public void update(@NotNull UserInfoData info) {
        this.valid = true;
        this.lastUpdate = System.currentTimeMillis();

        this.info = info;
    }


    @Override
    public @NotNull UserInfoData getInfo() {
        if (this.info != null && this.valid) {
            return this.info;
        } else {
            return UserInfoData.EMPTY;
        }
    }

}
