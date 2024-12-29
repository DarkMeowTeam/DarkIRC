package net.darkmeow.irc.network.packet.c2s;

import java.util.ArrayList;

public class C2SPacketQueryUsers implements C2SPacket {

    public ArrayList<String> names;

    public C2SPacketQueryUsers(ArrayList<String> names) {
        this.names = names;
    }

}
