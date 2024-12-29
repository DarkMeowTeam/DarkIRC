package net.darkmeow.irc.network.packet.s2c;

import net.darkmeow.irc.data.GameInfoData;

public class S2CPacketMessagePrivate implements S2CPacket {

    public String name;

    public String rank;

    public GameInfoData info;

    public String message;

    public S2CPacketMessagePrivate(String name, String rank, GameInfoData info, String message) {
        this.name = name;
        this.rank = rank;
        this.info = info;
        this.message = message;
    }

}
