package net.darkmeow.irc.network.packet.s2c;

import com.google.gson.JsonObject;

public class S2CPacketCustomPayload implements S2CPacket {

    public String payload;

    public JsonObject data;

    public S2CPacketCustomPayload(String payload, JsonObject data) {
        this.payload = payload;
        this.data = data;
    }

}
