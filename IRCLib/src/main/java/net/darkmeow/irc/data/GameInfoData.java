package net.darkmeow.irc.data;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class GameInfoData {

    public static GameInfoData EMPTY = new GameInfoData(new PlayerSessionData("", UUID.randomUUID()), ClientBrandData.EMPTY,null, null, 0, "", false);

    @NotNull
    public final PlayerSessionData session;

    @NotNull
    public final ClientBrandData client;

    @Nullable
    public final CustomSkinData skin;

    @Nullable
    public final String server;

    public final int clientFPS;

    @NotNull
    public final String namePrefix;

    public final boolean attackIRC;

    public GameInfoData(@NotNull PlayerSessionData session, @NotNull ClientBrandData client, @Nullable CustomSkinData skin, @Nullable String server, int clientFPS, @NotNull String namePrefix, boolean attackIRC) {
        this.session = session;
        this.client = client;
        this.skin = skin;
        this.server = server;
        this.clientFPS = clientFPS;
        this.namePrefix = namePrefix;
        this.attackIRC = attackIRC;
    }
}
