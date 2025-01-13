package net.darkmeow.irc.network.packet.c2s;

public class C2SPacketDisconnect implements C2SPacket {

    /**
     * 是否使 Token 失效
     */
    public boolean destroyToken;

    public C2SPacketDisconnect(boolean destroyToken) {
        this.destroyToken = destroyToken;
    }

}
