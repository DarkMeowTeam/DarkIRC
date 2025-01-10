package net.darkmeow.irc.client.data;

import net.darkmeow.irc.client.interfaces.data.IRCDataSessionInfo;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public abstract class DataSessionInfo implements IRCDataSessionInfo {

    public final UUID uniqueId;

    public boolean valid;

    public DataSessionInfo(@NotNull final UUID uniqueId) {
        this.uniqueId = uniqueId;
    }

    /**
     * 标记数据失效
     */
    public void markInvalid() {
        this.valid = false;
    }

    @Override
    public boolean isValid() {
        return this.valid;
    }

    @Override
    public @NotNull UUID getUniqueId() {
        return this.uniqueId;
    }

}
