package net.darkmeow.irc.client.enums;

import net.darkmeow.irc.network.packet.s2c.S2CPacketUpdateMyInfo;

public enum EnumPremium {
    GUEST,
    BANNED,
    USER,
    ADMIN,
    SUPER_ADMIN;

    public static EnumPremium getEnumPremiumFromPacket(S2CPacketUpdateMyInfo.Premium premium) {
        if (premium == S2CPacketUpdateMyInfo.Premium.GUEST) {
            return GUEST;
        } else if (premium == S2CPacketUpdateMyInfo.Premium.BANNED) {
            return BANNED;
        } else if (premium == S2CPacketUpdateMyInfo.Premium.USER) {
            return USER;
        } else if (premium == S2CPacketUpdateMyInfo.Premium.ADMIN) {
            return ADMIN;
        } else if (premium == S2CPacketUpdateMyInfo.Premium.SUPER_ADMIN) {
            return SUPER_ADMIN;
        }

        return GUEST;
    }
}