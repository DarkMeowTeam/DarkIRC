package net.darkmeow.irc.network.packet.s2c;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class S2CPacketLoginResult implements S2CPacket {

    @NotNull
    public LoginResult result;

    @Nullable
    public String token;

    public S2CPacketLoginResult(@NotNull LoginResult result) {
        this.result = result;
    }

    public S2CPacketLoginResult(@NotNull LoginResult result, @NotNull String token) {
        this.result = result;
        this.token = token;
    }

    public enum LoginResult {
        SUCCESS,
        USER_OR_PASSWORD_WRONG,
        INVALID_TOKEN,
        INVALID_CLIENT,
        OUTDATED_CLIENT_VERSION
    }

}
