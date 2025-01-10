package net.darkmeow.irc.client.enums;

import net.darkmeow.irc.network.packet.s2c.S2CPacketUpdateMySessionInfo;

public enum EnumPremium {
    GUEST,
    BANNED,
    USER,
    ADMIN,
    SUPER_ADMIN;

    public static EnumPremium getEnumPremiumFromPacket(S2CPacketUpdateMySessionInfo.Premium premium) {
        if (premium == S2CPacketUpdateMySessionInfo.Premium.GUEST) {
            return GUEST;
        } else if (premium == S2CPacketUpdateMySessionInfo.Premium.BANNED) {
            return BANNED;
        } else if (premium == S2CPacketUpdateMySessionInfo.Premium.USER) {
            return USER;
        } else if (premium == S2CPacketUpdateMySessionInfo.Premium.ADMIN) {
            return ADMIN;
        } else if (premium == S2CPacketUpdateMySessionInfo.Premium.SUPER_ADMIN) {
            return SUPER_ADMIN;
        }

        return GUEST;
    }
}