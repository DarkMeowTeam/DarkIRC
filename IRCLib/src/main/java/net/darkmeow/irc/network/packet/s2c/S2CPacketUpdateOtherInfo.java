package net.darkmeow.irc.network.packet.s2c;

import net.darkmeow.irc.data.GameInfoData;

public class S2CPacketUpdateOtherInfo implements S2CPacket {

    public String name;

    public String rank;

    public GameInfoData info;

    public S2CPacketUpdateOtherInfo(String name, String rank, GameInfoData info) {
        this.name = name;
        this.rank = rank;
        this.info = info;
    }

}
