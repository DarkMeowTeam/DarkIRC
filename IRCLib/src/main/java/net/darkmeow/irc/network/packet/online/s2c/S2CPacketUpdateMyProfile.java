package net.darkmeow.irc.network.packet.online.s2c;

import lombok.Getter;
import net.darkmeow.irc.data.enmus.EnumUserPremium;
import net.darkmeow.irc.network.FriendBuffer;
import net.darkmeow.irc.network.packet.S2CPacket;
import org.jetbrains.annotations.NotNull;


public class S2CPacketUpdateMyProfile implements S2CPacket {

    /**
     * 当前用户名
     */
    @Getter
    @NotNull
    private final String name;

    /**
     * 当前用户权限
     */
    @Getter
    @NotNull
    private final EnumUserPremium premium;

    /**
     * 是否为隐身登录模式
     */
    @Getter
    private final boolean invisible;

    public S2CPacketUpdateMyProfile(@NotNull String name, @NotNull EnumUserPremium premium, boolean invisible) {
        this.name = name;
        this.premium = premium;
        this.invisible = invisible;
    }

    public S2CPacketUpdateMyProfile(@NotNull FriendBuffer buffer) {
        this.name = buffer.readString(100);
        this.premium = buffer.readEnumValue(EnumUserPremium.class);
        this.invisible = buffer.readBoolean();
    }

    @Override
    public void write(@NotNull FriendBuffer buffer) {
        buffer.writeString(this.name);
        buffer.writeEnumValue(this.premium);
        buffer.writeBoolean(this.invisible);
    }
}
