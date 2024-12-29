package net.darkmeow.irc.network.packet.s2c;

public class S2CPacketUpdateMyInfo implements S2CPacket {

    public String name;

    public String rank;

    public Premium premium;

    public S2CPacketUpdateMyInfo(String name, String rank, Premium premium) {
        this.name = name;
        this.rank = rank;
        this.premium = premium;
    }

    public enum Premium {
        GUEST,
        BANNED,
        USER,
        ADMIN,
        SUPER_ADMIN;
    }
}
