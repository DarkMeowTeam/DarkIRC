package net.darkmeow.irc.network.packet.c2s;

import net.darkmeow.irc.data.GameInfoData;

public class C2SPacketUpdateGameInfo implements C2SPacket {

    public GameInfoData info;

    public C2SPacketUpdateGameInfo(GameInfoData info) {
        this.info = info;
    }

}
