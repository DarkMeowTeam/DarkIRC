package net.darkmeow.irc.network.packet.s2c;

import net.darkmeow.irc.data.UserInfoData;
import org.jetbrains.annotations.NotNull;

public class S2CPacketUpdateOtherInfo implements S2CPacket {

    @NotNull
    public String name;

    @NotNull
    public UserInfoData info;

    public S2CPacketUpdateOtherInfo(@NotNull String name, @NotNull UserInfoData info) {
        this.name = name;
        this.info = info;
    }

}
