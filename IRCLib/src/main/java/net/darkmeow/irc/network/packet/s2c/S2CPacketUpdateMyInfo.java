package net.darkmeow.irc.network.packet.s2c;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class S2CPacketUpdateMyInfo implements S2CPacket {

    @Nullable
    public String name;

    @Nullable
    public String rank;

    @NotNull
    public Premium premium;

    public S2CPacketUpdateMyInfo(@NotNull String name, @NotNull String rank, @NotNull Premium premium) {
        this.name = name;
        this.rank = rank;
        this.premium = premium;
    }

    public S2CPacketUpdateMyInfo() {
        this.name = null;
        this.rank = null;
        this.premium = Premium.GUEST;
    }

    public enum Premium {
        GUEST,
        BANNED,
        USER,
        ADMIN,
        SUPER_ADMIN;
    }
}
