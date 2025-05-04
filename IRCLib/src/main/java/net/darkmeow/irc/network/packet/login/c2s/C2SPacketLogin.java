package net.darkmeow.irc.network.packet.login.c2s;

import lombok.Getter;
import net.darkmeow.irc.network.FriendBuffer;
import net.darkmeow.irc.network.packet.C2SPacket;
import org.jetbrains.annotations.NotNull;

public class C2SPacketLogin implements C2SPacket {

    @Getter
    @NotNull
    private final String username;

    /**
     * 密码/Token
     */
    @Getter
    @NotNull
    private final String password;

    /**
     * 隐身登录
     */
    @Getter
    private final boolean invisible;

    /**
     * 关闭生成新 token
     */
    @Getter
    private final boolean disableGenerateToken;


    public C2SPacketLogin(@NotNull String username, @NotNull String password) {
        this.username = username;
        this.password = password;
        this.invisible = false;
        this.disableGenerateToken = false;
    }

    public C2SPacketLogin(@NotNull String username, @NotNull String password, boolean invisible) {
        this.username = username;
        this.password = password;
        this.invisible = invisible;
        this.disableGenerateToken = false;
    }

    public C2SPacketLogin(@NotNull String username, @NotNull String password, boolean invisible, boolean disableGenerateToken) {
        this.username = username;
        this.password = password;
        this.invisible = invisible;
        this.disableGenerateToken = disableGenerateToken;
    }

    public C2SPacketLogin(@NotNull FriendBuffer buffer) {
        this.username = buffer.readString(100);
        this.password = buffer.readString(1024);
        this.invisible = buffer.readBoolean();
        this.disableGenerateToken = buffer.readBoolean();
    }

    @Override
    public void write(@NotNull FriendBuffer buffer) {
        buffer.writeString(this.username);
        buffer.writeString(this.password);
        buffer.writeBoolean(this.invisible);
        buffer.writeBoolean(this.disableGenerateToken);
    }
}
