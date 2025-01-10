package net.darkmeow.irc.client.manager;

import net.darkmeow.irc.client.data.DataSelfInfo;
import net.darkmeow.irc.client.data.IRCUserInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.UUID;

public class IRCClientUserManager {

    @Nullable
    public DataSelfInfo selfInfo;

    @NotNull
    public HashMap<UUID, IRCUserInfo> users = new HashMap<>();

}
