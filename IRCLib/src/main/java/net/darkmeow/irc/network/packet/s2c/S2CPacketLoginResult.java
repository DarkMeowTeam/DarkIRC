package net.darkmeow.irc.network.packet.s2c;

public class S2CPacketLoginResult implements S2CPacket {

    public LoginResult result;

    public S2CPacketLoginResult(LoginResult result) {
        this.result = result;
    }

    public enum LoginResult {
        SUCCESS,
        USER_OR_PASSWORD_WRONG,
        INVALID_CLIENT,
        OUTDATED_CLIENT_VERSION
    }

}
