package net.darkmeow.irc.network.packet.c2s;

import net.darkmeow.irc.data.DataSessionOptions;

public class C2SPacketUpdateSessionOptions implements C2SPacket {

    public DataSessionOptions options;

    public C2SPacketUpdateSessionOptions(DataSessionOptions options) {
        this.options = options;
    }

}
