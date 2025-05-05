package net.darkmeow.irc.network.packet.handshake.c2s;

import lombok.Getter;
import net.darkmeow.irc.data.DataClientBrand;
import net.darkmeow.irc.network.FriendBuffer;
import net.darkmeow.irc.network.packet.C2SPacket;
import org.jetbrains.annotations.NotNull;

/**
 * 握手包
 */
public class C2SPacketHandShake implements C2SPacket {

    /**
     * 协议版本号
     */
    @Getter
    private final int protocolVersion;

    /**
     * 连接 IRC 服务器用的地址
     */
    @Getter
    @NotNull
    private final String host;

    /**
     * 连接 IRC 服务器用的端口
     */
    @Getter
    private final int port;

    /**
     * 连接客户端设备唯一标识
     */
    @Getter
    @NotNull
    private final String hardWareUniqueId;

    /**
     * 连接客户端标识
     */
    @Getter
    @NotNull
    private final DataClientBrand brand;

    /**
     * 客户端系统时间
     */
    @Getter
    private final long timestamp;

    public C2SPacketHandShake(int protocolVersion, @NotNull String host, int port, @NotNull String hardWareUniqueId, @NotNull DataClientBrand brand, long timestamp) {
        this.protocolVersion = protocolVersion;
        this.host = host;
        this.port = port;
        this.hardWareUniqueId = hardWareUniqueId;
        this.brand = brand;
        this.timestamp = timestamp;
    }

    public C2SPacketHandShake(@NotNull FriendBuffer buffer) {
        this.protocolVersion = buffer.readInt();
        this.host = buffer.readString(32767);
        this.port = buffer.readInt();
        this.hardWareUniqueId = buffer.readString(32767);
        this.brand = buffer.readClientBrand();
        this.timestamp = buffer.readLong();
    }

    @Override
    public void write(@NotNull FriendBuffer buffer) {
        buffer.writeInt(this.protocolVersion);
        buffer.writeString(this.host);
        buffer.writeInt(this.port);
        buffer.writeString(this.hardWareUniqueId);
        buffer.writeClientBrand(this.brand);
        buffer.writeLong(this.timestamp);
    }
}
