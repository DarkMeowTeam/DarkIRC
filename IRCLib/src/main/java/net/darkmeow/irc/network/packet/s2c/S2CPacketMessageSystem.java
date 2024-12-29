package net.darkmeow.irc.network.packet.s2c;

public class S2CPacketMessageSystem implements S2CPacket {

    public String message;

    public S2CPacketMessageSystem(String message) {
        this.message = message;
    }

}
