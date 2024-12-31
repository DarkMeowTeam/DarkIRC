package net.darkmeow.irc.data;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PlayerSessionData {

    @NotNull
    public final String name;

    @NotNull
    public final UUID id;

    public PlayerSessionData(@NotNull String name, @NotNull UUID id) {
        this.name = name;
        this.id = id;
    }

}
