package net.darkmeow.irc.network.packet.online.s2c;

import lombok.Getter;
import net.darkmeow.irc.network.FriendBuffer;
import net.darkmeow.irc.network.packet.S2CPacket;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class S2CPacketPrivateMessageResult implements S2CPacket {

    /**
     * 接收者标识 和 C2SPacketMessage 参数数据一样
     */
    @Getter
    @NotNull
    private final String receiver;

    /**
     * 消息内容
     */
    @Getter
    @Nullable
    private final String message;

    /**
     * 消息唯一ID
     */
    @Getter
    @Nullable
    private final UUID id;

    /**
     * 私聊消息发送失败
     * 
     * @param receiver 欲发送用户名
     */
    public S2CPacketPrivateMessageResult(@NotNull String receiver) {
        this.receiver = receiver;
        this.message = null;
        this.id = null;
    }

    /**
     * 私聊消息发送成功
     *
     * @param receiver 欲发送用户名
     * @param message 消息内容
     * @param id 消息标识
     */
    public S2CPacketPrivateMessageResult(@NotNull String receiver, @NotNull String message, @NotNull UUID id) {
        this.receiver = receiver;
        this.message = message;
        this.id = id;
    }

    public S2CPacketPrivateMessageResult(@NotNull FriendBuffer buffer) {
        this.receiver = buffer.readString(32767);
        if (buffer.readBoolean()) {
            this.message = buffer.readString(32767);
            this.id = buffer.readUniqueId();
        } else {
            this.message = null;
            this.id = null;
        }
    }

    @Override
    public void write(@NotNull FriendBuffer buffer) {
        buffer.writeString(this.receiver);
        if (this.message != null && this.id != null) {
            buffer.writeBoolean(true);
            buffer.writeString(this.message);
            buffer.writeUniqueId(this.id);
        } else {
            buffer.writeBoolean(false);
        }
    }

    public boolean isSuccess() {
        return this.message != null && this.id != null;
    }
}
