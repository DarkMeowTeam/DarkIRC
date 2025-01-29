package net.darkmeow.irc.network.packet.s2c;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class S2CPacketUpdateMySessionInfo implements S2CPacket {

    @NotNull
    public String name;

    @NotNull
    public String rank;

    @NotNull
    public Premium premium;

    public boolean invisible;

    @NotNull
    public UUID sessionUniqueId;

    public S2CPacketUpdateMySessionInfo(@NotNull String name, @NotNull String rank, @NotNull Premium premium, boolean invisible, @NotNull UUID sessionUniqueId) {
        this.name = name;
        this.rank = rank;
        this.premium = premium;
        this.invisible = invisible;
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
