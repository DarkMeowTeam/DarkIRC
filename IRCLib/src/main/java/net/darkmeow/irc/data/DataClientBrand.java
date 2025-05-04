package net.darkmeow.irc.data;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

public class DataClientBrand {

    @Getter
    @NotNull
    private final String name;

    @Getter
    @NotNull
    private final String key;

    @Getter
    @NotNull
    private final String versionText;

    @Getter
    private final int versionId;

    public DataClientBrand(@NotNull String name, @NotNull String key, @NotNull String versionText, int versionId) {
        this.name = name;
        this.key = key;
        this.versionText = versionText;
        this.versionId = versionId;
    }
}
