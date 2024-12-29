package net.darkmeow.irc.network.packet.c2s;

public class C2SPacketChatPrivate implements C2SPacket {

    public String user;

    public String message;

    public C2SPacketChatPrivate(String user, String message) {
        this.user = user;
        this.message = message;
    }

}
