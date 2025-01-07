package net.darkmeow.irc.network.packet.s2c;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class S2CPacketUpdateMyInfo implements S2CPacket {

    @NotNull
    public String name;

    @NotNull
    public String rank;

    @NotNull
    public Premium premium;

    @NotNull
    public UUID sessionUniqueId;

    public S2CPacketUpdateMyInfo(@NotNull String name, @NotNull String rank, @NotNull Premium premium, @NotNull UUID sessionUniqueId) {
        this.name = name;
        this.rank = rank;
        this.premium = premium;
        this.sessionUniqueId = sessionUniqueId;
    }

    public enum Premium {
        GUEST,
        BANNED,
        USER,
        ADMIN,
        SUPER_ADMIN;
    }
}
