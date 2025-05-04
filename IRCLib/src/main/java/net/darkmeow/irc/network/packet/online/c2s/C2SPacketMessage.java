package net.darkmeow.irc.network.packet.online.c2s;

import lombok.Getter;
import net.darkmeow.irc.network.FriendBuffer;
import net.darkmeow.irc.network.packet.C2SPacket;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class C2SPacketMessage implements C2SPacket {

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
         * 系统指令
         */
        COMMAND
    }

    @Getter
    @NotNull
    public final List<String> arg;

    @Getter
    @NotNull
    public final String message;


    public C2SPacketMessage(@NotNull String message) {
        this.type = Type.PUBLIC;
        this.arg = new ArrayList<>();
        this.message = message;
    }

    public C2SPacketMessage(@NotNull String toUser, @NotNull String message) {
        this.type = Type.PRIVATE;
        this.arg = new ArrayList<>(Collections.singleton(toUser));
        this.message = message;
    }

    public C2SPacketMessage(@NotNull String root, @NotNull List<String> args) {
        this.type = Type.COMMAND;
        this.arg = args;
        this.message = root;
    }

    public C2SPacketMessage(@NotNull FriendBuffer buffer) {
        this.type = buffer.readEnumValue(Type.class);

        final int size = buffer.readInt();
        this.arg = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            this.arg.add(buffer.readString(32767));
        }

        this.message = buffer.readString(32767);
    }

    @Override
    public void write(@NotNull FriendBuffer buffer) {
        buffer.writeEnumValue(this.type);

        buffer.writeInt(this.arg.size());
        for (String s : this.arg) {
            buffer.writeString(s);
        }

        buffer.writeString(this.message);
    }
}
