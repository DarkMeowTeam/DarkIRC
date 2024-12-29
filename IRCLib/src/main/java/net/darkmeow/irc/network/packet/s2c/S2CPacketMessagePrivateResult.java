package net.darkmeow.irc.network.packet.s2c;

public class S2CPacketMessagePrivateResult implements S2CPacket {

    public String name;

    public String message;

    public boolean success;

    public S2CPacketMessagePrivateResult(String name, String message, boolean success) {
        this.name = name;
        this.message = message;
        this.success = success;
    }

}
