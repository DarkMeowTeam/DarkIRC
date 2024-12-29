package net.darkmeow.irc.network.packet.c2s;

public class C2SPacketKeepAlive implements C2SPacket {

    public long id;

    public C2SPacketKeepAlive(long id) {
        this.id = id;
    }

}
