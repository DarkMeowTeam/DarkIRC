package net.darkmeow.irc.network.packet.c2s;

import java.util.ArrayList;

public class C2SPacketCommand implements C2SPacket {

    public String root;

    public ArrayList<String> args;

    public C2SPacketCommand(String root, ArrayList<String> args) {
        this.root = root;
        this.args = args;
    }
}
