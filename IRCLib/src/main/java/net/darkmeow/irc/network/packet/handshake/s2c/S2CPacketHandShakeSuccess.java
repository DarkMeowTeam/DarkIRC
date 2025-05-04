package net.darkmeow.irc.network.packet.handshake.s2c;

import lombok.Getter;
import net.darkmeow.irc.network.FriendBuffer;
import net.darkmeow.irc.network.packet.S2CPacket;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * 握手包成功
 * 当客户端接收到此包时 需要立即切换连接状态至 LOGIN
 */
public class S2CPacketHandShakeSuccess implements S2CPacket {

    @Getter
    @NotNull
    private final UUID sessionId;

    public S2CPacketHandShakeSuccess(@NotNull UUID sessionId) {
        this.sessionId = sessionId;
    }

    public S2CPacketHandShakeSuccess(@NotNull FriendBuffer buffer) {
        this.sessionId = buffer.readUniqueId();
    }

    @Override
    public void write(@NotNull FriendBuffer buffer) {
        buffer.writeUniqueId(sessionId);
    }
}
