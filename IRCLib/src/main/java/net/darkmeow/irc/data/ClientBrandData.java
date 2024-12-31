package net.darkmeow.irc.data;

import org.jetbrains.annotations.NotNull;

public class ClientBrandData {

    public static ClientBrandData EMPTY = new ClientBrandData("", "", 0);

    @NotNull
    public final String id;

    @NotNull
    public final String hash;

    public final int versionId;

    @NotNull
    public final String versionName;

    public ClientBrandData(@NotNull String id, @NotNull String hash, int versionId, @NotNull String versionName) {
        this.id = id;
        this.hash = hash;
        this.versionId = versionId;
        this.versionName = versionName;
    }

    public ClientBrandData(@NotNull String id, @NotNull String hash, int version) {
        this.id = id;
        this.hash = hash;
        this.versionId = version;
        this.versionName = String.valueOf(version);
    }
}
