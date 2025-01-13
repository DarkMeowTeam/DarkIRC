package net.darkmeow.irc.network;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.darkmeow.irc.network.packet.c2s.*;
import net.darkmeow.irc.network.packet.s2c.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class PacketUtils {
    private static final Gson gson = new Gson();

    public static final HashMap<String, Class<? extends C2SPacket>> clientPackets = new HashMap<>();
    public static final HashMap<String, Class<? extends S2CPacket>> serverPackets = new HashMap<>();

    static {
        clientPackets.put("HandShake", C2SPacketHandShake.class);
        clientPackets.put("KeepAlive", C2SPacketKeepAlive.class);
        clientPackets.put("Login", C2SPacketLogin.class);
        clientPackets.put("ChatPublic", C2SPacketChatPublic.class);
        clientPackets.put("ChatPrivate", C2SPacketChatPrivate.class);
        clientPackets.put("Command", C2SPacketCommand.class);
        clientPackets.put("UpdateSessionOptions", C2SPacketUpdateSessionOptions.class);
        clientPackets.put("QueryUsers", C2SPacketQueryUsers.class);
        clientPackets.put("ChangePassword", C2SPacketChangePassword.class);
        clientPackets.put("Disconnect", C2SPacketDisconnect.class);
        clientPackets.put("CustomPayload", C2SPacketCustomPayload.class);

        serverPackets.put("HandShake", S2CPacketHandShake.class);
        serverPackets.put("KeepAlive", S2CPacketKeepAlive.class);
        serverPackets.put("LoginResult", S2CPacketLoginResult.class);
        serverPackets.put("UpdateMySessionInfo", S2CPacketUpdateMySessionInfo.class);
        serverPackets.put("MessageSystem", S2CPacketMessageSystem.class);
        serverPackets.put("MessagePublic", S2CPacketMessagePublic.class);
        serverPackets.put("MessagePrivate", S2CPacketMessagePrivate.class);
        serverPackets.put("MessagePrivateResult", S2CPacketMessagePrivateResult.class);
        serverPackets.put("UpdateOtherSessionInfo", S2CPacketUpdateOtherSessionInfo.class);
        serverPackets.put("UpdateMultiSessionInfo", S2CPacketUpdateMultiSessionInfo.class);
        serverPackets.put("Disconnect", S2CPacketDisconnect.class);
        serverPackets.put("CustomPayload", S2CPacketCustomPayload.class);
    }


    public static @Nullable C2SPacket resolveClientPacket(@NotNull JsonObject obj) {
        final Class<? extends C2SPacket> clazz = clientPackets.get(obj.get("type").getAsString());

        if (clazz != null) {
            return gson.fromJson(obj.get("data"), clazz);
        } else {
            return null;
        }
    }

    public static @Nullable S2CPacket resolveServerPacket(@NotNull JsonObject obj) {
        final Class<? extends S2CPacket> clazz = serverPackets.get(obj.get("type").getAsString());

        if (clazz != null) {
            return gson.fromJson(obj.get("data"), clazz);
        } else {
            return null;
        }
    }

    public static @NotNull String generatePacket(@NotNull C2SPacket packet) {
        final JsonObject json = new JsonObject();

        final String type = clientPackets.entrySet().stream()
            .filter(it -> it.getValue() == packet.getClass())
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("No packet type found for " + packet.getClass().getSimpleName()))
            .getKey();


        json.addProperty("type", type);
        json.add("data", gson.toJsonTree(packet, packet.getClass()));
        json.addProperty("time", System.currentTimeMillis());

        return json.toString();
    }

    public static @NotNull String generatePacket(@NotNull S2CPacket packet) {
        final JsonObject json = new JsonObject();

        final String type = serverPackets.entrySet().stream()
            .filter(it -> it.getValue() == packet.getClass())
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("No packet type found for " + packet.getClass().getSimpleName()))
            .getKey();


        json.addProperty("type", type);
        json.add("data", gson.toJsonTree(packet, packet.getClass()));
        json.addProperty("time", System.currentTimeMillis());

        return json.toString();
    }
}
