package net.darkmeow.irc.network.packet.c2s;

public class C2SPacketQueryUsers implements C2SPacket {

    public boolean onlySameServer;

    public C2SPacketQueryUsers(boolean onlySameServer) {
        this.onlySameServer = onlySameServer;
    }

}
