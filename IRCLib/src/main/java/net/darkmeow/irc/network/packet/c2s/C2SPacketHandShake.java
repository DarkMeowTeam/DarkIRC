package net.darkmeow.irc.network.packet.c2s;

public class C2SPacketHandShake implements C2SPacket {

    public int protocolVersion;

    public String deviceId;

    public C2SPacketHandShake(int protocolVersion, String deviceId) {
        this.protocolVersion = protocolVersion;
        this.deviceId = deviceId;
    }

}
