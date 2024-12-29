package net.darkmeow.irc.network.packet.s2c;

public class S2CPacketKeepAlive implements S2CPacket {

    public long id;

    public S2CPacketKeepAlive(long id) {
        this.id = id;
    }

}
