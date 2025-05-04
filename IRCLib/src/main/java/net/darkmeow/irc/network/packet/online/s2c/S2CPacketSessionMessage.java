package net.darkmeow.irc.network.packet.online.s2c;

import lombok.Getter;
import net.darkmeow.irc.data.DataUser;
import net.darkmeow.irc.network.FriendBuffer;
import net.darkmeow.irc.network.packet.S2CPacket;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class S2CPacketSessionMessage implements S2CPacket {

    /**
     * 消息类型
     */
    @Getter
    @NotNull
    private final Type type;

    public enum Type {
        /**
         * 公开聊天
         */
        PUBLIC,
        /**
         * 私有聊天
         */
        PRIVATE
    }


    /**
     * 发送者唯一标识
     */
    @Getter
    @NotNull
    private final UUID sender;

    /**
     * 发送者标识
     */
    @Getter
    @NotNull
    private final DataUser senderData;

    /**
     * 消息内容
     */
    @Getter
    @NotNull
    private final String message;

    /**
     * 消息唯一ID
     */
    @Getter
    @NotNull
    private final UUID id;

    public S2CPacketSessionMessage(@NotNull Type type, @NotNull UUID sender, @NotNull DataUser senderData, @NotNull String message, @NotNull UUID id) {
        this.type = type;
        this.sender = sender;
        this.senderData = senderData;
        this.message = message;
        this.id = id;
    }

    public S2CPacketSessionMessage(@NotNull FriendBuffer buffer) {
        this.type = buffer.readEnumValue(Type.class);
        this.sender = buffer.readUniqueId();
        this.senderData = buffer.readUser();
        this.message = buffer.readString(32767);
        this.id = buffer.readUniqueId();
    }

    @Override
    public void write(@NotNull FriendBuffer buffer) {
        buffer.writeEnumValue(this.type);
        buffer.writeUniqueId(this.sender);
        buffer.writeUser(this.senderData);
        buffer.writeString(this.message);
        buffer.writeUniqueId(this.id);
    }
}
