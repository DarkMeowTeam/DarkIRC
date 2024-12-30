package net.darkmeow.irc.client.enums;

public enum EnumResultLogin {
    /**
     * 登录成功
     */
    SUCCESS,
    /**
     * 客户端已停用
     */
    INVALID_CLIENT,
    /**
     * 客户端版本过低
     */
    OUTDATED_CLIENT_VERSION,
    /**
     * 用户名或密码错误
     */
    USER_OR_PASSWORD_WRONG,
    /**
     * 未连接服务器
     */
    NOT_CONNECT
}
