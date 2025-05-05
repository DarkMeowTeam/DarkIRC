package net.darkmeow.irc.network.packet.online.s2c;

import lombok.Getter;
import net.darkmeow.irc.network.FriendBuffer;
import net.darkmeow.irc.network.packet.S2CPacket;
import org.jetbrains.annotations.NotNull;

public class S2CPacketDisconnect implements S2CPacket {

    /**
     * 将客户端会话密钥标记为失效
     * (登录状态失效, 请你重新登录)
     */
    @Getter
    private final boolean markSessionTokenInvalid;
    /**
     * 断开原因
     */
    @Getter
    @NotNull
    private final String message;

    public S2CPacketDisconnect(boolean markSessionTokenInvalid, @NotNull String message) {
        this.markSessionTokenInvalid = markSessionTokenInvalid;
        this.message = message;
    }

    public S2CPacketDisconnect(@NotNull FriendBuffer buffer) {
        this.markSessionTokenInvalid = buffer.readBoolean();
        this.message = buffer.readString(32767);
    }

    @Override
    public void write(@NotNull FriendBuffer buffer) {
        buffer.writeBoolean(this.markSessionTokenInvalid);
        buffer.writeString(this.message);
    }
}
