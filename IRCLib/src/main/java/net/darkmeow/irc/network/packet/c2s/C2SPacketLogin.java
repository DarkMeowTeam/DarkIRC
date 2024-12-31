package net.darkmeow.irc.network.packet.c2s;

import net.darkmeow.irc.data.ClientBrandData;

public class C2SPacketLogin implements C2SPacket {

    public String name;

    public String password;

    public ClientBrandData client;

    public C2SPacketLogin(String name, String password, ClientBrandData client) {
        this.name = name;
        this.password = password;
        this.client = client;
    }

}
