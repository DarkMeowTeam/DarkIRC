package net.darkmeow.irc.network.packet.s2c;

import net.darkmeow.irc.data.UserInfoData;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class S2CPacketMessagePrivate implements S2CPacket {

    @NotNull
    public UUID sessionUniqueId;

    @NotNull
    public UserInfoData info;

    @NotNull
    public String message;

    public S2CPacketMessagePrivate(@NotNull UUID sessionUniqueId, @NotNull UserInfoData info, @NotNull String message) {
        this.sessionUniqueId = sessionUniqueId;
        this.info = info;
        this.message = message;
    }

}
