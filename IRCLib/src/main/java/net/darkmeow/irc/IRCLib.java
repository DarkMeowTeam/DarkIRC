package net.darkmeow.irc;

public class IRCLib {
    /**
     * IRC协议版本号
     * 只有在更新版本无法向下兼容时才会递增
     * 更新历史:
     * 1 -> 单用户多会话兼容
     * 2 -> 客户端信息与客户端标识数据分开传输
     */
    public static final int PROTOCOL_VERSION = 2;
}
