package net.darkmeow.irc.data;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class DataSessionInfo {

    public static DataSessionInfo EMPTY = new DataSessionInfo(ClientBrandData.EMPTY);

    @NotNull
    public final ClientBrandData brand;


    public DataSessionInfo(@NotNull ClientBrandData brand) {
        this.brand = brand;
    }
}
