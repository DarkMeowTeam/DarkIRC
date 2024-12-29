package net.darkmeow.irc.network.packet.s2c;

import java.util.ArrayList;

public class S2CPacketUpdateExcludeNames implements S2CPacket {

    public ArrayList<String> names;

    public S2CPacketUpdateExcludeNames(ArrayList<String> names) {
        this.names = names;
    }

}
