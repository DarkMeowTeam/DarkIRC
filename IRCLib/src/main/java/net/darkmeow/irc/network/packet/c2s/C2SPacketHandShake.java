package net.darkmeow.irc.network.packet.c2s;

public class C2SPacketHandShake implements C2SPacket {

    public int protocolVersion;

    public C2SPacketHandShake(int protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

}
