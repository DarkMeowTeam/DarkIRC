package net.darkmeow.irc.network.packet.s2c;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.UUID;

public class S2CPacketMessagePrivateResult implements S2CPacket {

    public boolean success;

    @NotNull
    public String name;

    @NotNull
    public String message;

    @Nullable
    public ArrayList<UUID> sessions;

    public S2CPacketMessagePrivateResult(@NotNull String name, @NotNull String message) {
        this.success = false;
        this.name = name;
        this.message = message;
    }

    public S2CPacketMessagePrivateResult(@NotNull String name, @NotNull String message, @NotNull ArrayList<UUID> sessions) {
        this.success = true;
        this.name = name;
        this.message = message;
        this.sessions = sessions;
    }

}
