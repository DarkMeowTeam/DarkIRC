package net.darkmeow.irc.network.packet.s2c;

public class S2CPacketDisconnect implements S2CPacket {

    public String reason;

    public S2CPacketDisconnect(String reason) {
        this.reason = reason;
    }

}
