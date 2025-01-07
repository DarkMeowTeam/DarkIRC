package net.darkmeow.irc.network.packet.s2c;

public class S2CPacketDisconnect implements S2CPacket {

    /**
     * 服务端断开连接理由
     */
    public String reason;

    /**
     * 是否同时退出登录
     * 可用于改密下线等
     */
    public boolean logout;

    public S2CPacketDisconnect(String reason) {
        this.reason = reason;
        this.logout = true;
    }

    public S2CPacketDisconnect(String reason, boolean logout) {
        this.reason = reason;
        this.logout = logout;
    }

}
