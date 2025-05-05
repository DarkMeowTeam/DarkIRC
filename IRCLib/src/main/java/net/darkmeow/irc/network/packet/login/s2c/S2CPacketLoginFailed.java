package net.darkmeow.irc.network.packet.login.s2c;

import lombok.Getter;
import net.darkmeow.irc.network.FriendBuffer;
import net.darkmeow.irc.network.packet.S2CPacket;
import org.jetbrains.annotations.NotNull;

public class S2CPacketLoginFailed implements S2CPacket {

    @Getter
    @NotNull
    private final String reason;
    /**
     * 将客户端会话密钥标记为失效
     * (登录状态失效, 请你重新登录)
     */
    @Getter
    private final boolean markSessionTokenInvalid;

    public S2CPacketLoginFailed(@NotNull String reason, boolean markSessionTokenInvalid) {
        this.reason = reason;
        this.markSessionTokenInvalid = markSessionTokenInvalid;
    }

    public S2CPacketLoginFailed(@NotNull FriendBuffer buffer) {
        this.reason = buffer.readString(32767);
        this.markSessionTokenInvalid = buffer.readBoolean();
    }

    @Override
    public void write(@NotNull FriendBuffer buffer) {
        buffer.writeString(reason);
        buffer.writeBoolean(markSessionTokenInvalid);
    }
}
