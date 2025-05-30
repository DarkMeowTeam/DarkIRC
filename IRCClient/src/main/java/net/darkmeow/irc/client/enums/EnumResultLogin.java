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
     * 无效的平局
     */
    INVALID_TOKEN,
    /**
     * 无权限登录当前客户端
     */
    NO_PREMIUM_LOGIN_THIS_CLIENT,
    /**
     * 未连接服务器
     */
    NOT_CONNECT,
    /**
     * 登录超时
     */
    TIME_OUT
}
