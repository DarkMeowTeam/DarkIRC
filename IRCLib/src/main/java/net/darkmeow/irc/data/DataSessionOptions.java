package net.darkmeow.irc.data;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class DataSessionOptions {

    public static DataSessionOptions EMPTY = new DataSessionOptions(new PlayerSessionData("", UUID.randomUUID()),null, null, 0, "", false);

    @NotNull
    public final PlayerSessionData session;

    @Nullable
    public final CustomSkinData skin;

    @Nullable
    public final String server;

    public final int clientFPS;

    @NotNull
    public final String namePrefix;

    public final boolean attackIRC;

    public DataSessionOptions(@NotNull PlayerSessionData session, @Nullable CustomSkinData skin, @Nullable String server, int clientFPS, @NotNull String namePrefix, boolean attackIRC) {
        this.session = session;
        this.skin = skin;
        this.server = server;
        this.clientFPS = clientFPS;
        this.namePrefix = namePrefix;
        this.attackIRC = attackIRC;
    }
}
