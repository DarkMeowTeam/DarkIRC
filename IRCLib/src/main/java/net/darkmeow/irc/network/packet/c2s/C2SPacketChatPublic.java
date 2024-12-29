package net.darkmeow.irc.network.packet.c2s;

public class C2SPacketChatPublic implements C2SPacket {

    public String message;

    public C2SPacketChatPublic(String message) {
        this.message = message;
    }

}
