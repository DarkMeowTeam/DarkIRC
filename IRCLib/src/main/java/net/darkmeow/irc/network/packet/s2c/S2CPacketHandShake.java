package net.darkmeow.irc.network.packet.s2c;

public class S2CPacketHandShake implements S2CPacket {

    public int protocolVersion;

    public S2CPacketHandShake(int protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

}
