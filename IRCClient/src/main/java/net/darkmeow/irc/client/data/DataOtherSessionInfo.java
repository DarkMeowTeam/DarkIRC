package net.darkmeow.irc.client.data;

import lombok.Getter;
import net.darkmeow.irc.client.interfaces.data.IRCDataOtherSessionInfo;
import net.darkmeow.irc.data.DataUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public final class DataOtherSessionInfo extends DataSessionInfo implements IRCDataOtherSessionInfo {

    public long lastUpdate;

    @Getter
    @Nullable
    public DataUser info;

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
    public void update(@NotNull DataUser info) {
        this.valid = true;
        this.lastUpdate = System.currentTimeMillis();

        this.info = info;
    }

}
