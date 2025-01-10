package net.darkmeow.irc.client.manager;

import net.darkmeow.irc.client.data.DataSelfSessionInfo;
import net.darkmeow.irc.client.data.DataOtherSessionInfo;
import net.darkmeow.irc.client.interfaces.data.IRCDataOtherSessionInfo;
import net.darkmeow.irc.client.interfaces.data.IRCDataSelfSessionInfo;
import net.darkmeow.irc.client.interfaces.manager.IRCSessionManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager implements IRCSessionManager {

    @Nullable
    public DataSelfSessionInfo self;

    @NotNull
    public ConcurrentHashMap<UUID, DataOtherSessionInfo> users = new ConcurrentHashMap<>();

    public void clearInvalidUsers() {
        users.entrySet().removeIf(entry -> !entry.getValue().isValid());
    }

    public void reset() {
        self = null;
        users.forEach((uuid, info) -> {
            info.markInvalid();
        });
        users.clear();
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
