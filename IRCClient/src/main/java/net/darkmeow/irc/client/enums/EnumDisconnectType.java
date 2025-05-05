package net.darkmeow.irc.client.enums;

public enum EnumDisconnectType {
    /**
     * 用户手动断开
     */
    DISCONNECT_BY_USER,
    /**
     * 登录失败
     */
    FAILED_TO_LOGIN,
    /**
     * 被服务器踢出
     */
    KICK_BY_SERVER,
    /**
     * 其它
     * 主要为意外断开连接
     */
    OTHER
}
