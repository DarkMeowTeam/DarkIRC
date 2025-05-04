package net.darkmeow.irc.network.packet.online.c2s;

import lombok.Getter;
import net.darkmeow.irc.network.FriendBuffer;
import net.darkmeow.irc.network.packet.C2SPacket;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class C2SPacketInputStatus implements C2SPacket {

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
        PRIVATE,
        /**
         * 清空正在输入状态
         */
        CLEAR
    }

    @Getter
    @NotNull
    public final String receiver;

    @Getter
    @NotNull
    public final String message;

    /**
     * 上报正在输入状态 - 清空
     */
    public C2SPacketInputStatus() {
        this.type = Type.CLEAR;
        this.receiver = "";
        this.message = "";
    }

    /**
     * 上报正在输入状态 - 公开聊天
     */
    public C2SPacketInputStatus(@NotNull String message) {
        this.type = Type.PUBLIC;
        this.receiver = "";
        this.message = message;
    }

    /**
     * 上报正在输入状态 - 私有聊天
     */
    public C2SPacketInputStatus(@NotNull String receiver, @NotNull String message) {
        this.type = Type.PRIVATE;
        this.receiver = receiver;
        this.message = message;
    }

    public C2SPacketInputStatus(@NotNull FriendBuffer buffer) {
        this.type = buffer.readEnumValue(Type.class);
        this.receiver = buffer.readString(32767);
        this.message = buffer.readString(32767);
    }

    @Override
    public void write(@NotNull FriendBuffer buffer) {
        buffer.writeEnumValue(this.type);
        buffer.writeString(this.receiver);
        buffer.writeString(this.message);
    }
}
