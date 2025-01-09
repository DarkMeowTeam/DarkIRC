package net.darkmeow.irc.client.enums;

public enum EnumDisconnectType {
    /**
     * 用户手动断开
     */
    DISCONNECT_BY_USER,
    /**
     * 被服务器踢出
     */
    KICK_BY_SERVER,
    /**
     * 其它
     */
    OTHER
}
