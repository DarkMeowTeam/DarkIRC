package net.darkmeow.irc.data;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class DataPlayInGameProfile {

    @Getter
    @NotNull
    private final String name;

    @Getter
    @NotNull
    private final UUID id;

    public DataPlayInGameProfile(@NotNull String name, @NotNull UUID id) {
        this.name = name;
        this.id = id;
    }
}
