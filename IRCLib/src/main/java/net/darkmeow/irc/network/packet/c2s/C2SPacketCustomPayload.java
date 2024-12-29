package net.darkmeow.irc.network.packet.c2s;

import com.google.gson.JsonObject;

public class C2SPacketCustomPayload implements C2SPacket {

    public String payload;

    public JsonObject data;

    public C2SPacketCustomPayload(String payload, JsonObject data) {
        this.payload = payload;
        this.data = data;
    }

}
