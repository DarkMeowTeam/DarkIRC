package net.darkmeow.irc.client.manager;

import lombok.Getter;
import net.darkmeow.irc.client.data.DataSelfSessionInfo;
import net.darkmeow.irc.client.data.DataOtherSessionInfo;
import net.darkmeow.irc.client.interfaces.data.IRCDataOtherSessionInfo;
import net.darkmeow.irc.client.interfaces.data.IRCDataSelfSessionInfo;
import net.darkmeow.irc.client.interfaces.manager.IRCSessionManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class SessionManager implements IRCSessionManager {

    @Getter
    @Nullable
    public UUID sessionId;

    @Nullable
    public DataSelfSessionInfo self;

    @NotNull
    public ConcurrentHashMap<UUID, DataOtherSessionInfo> users = new ConcurrentHashMap<>();

    public void clearInvalidUsers() {
        users.entrySet().removeIf(entry -> !entry.getValue().isValid());
    }

    public void reset(UUID sessionId) {
        this.self = null;
        this.users.forEach((uuid, info) -> info.markInvalid());
        this.users.clear();
        this.sessionId = sessionId;
    }

    @Override
    public @Nullable IRCDataSelfSessionInfo getSelfSession() {
        return this.self;
    }

    @Override
    public @NotNull ConcurrentHashMap<UUID, ? extends IRCDataOtherSessionInfo> getSessions() {
        return this.users;
    }
}
