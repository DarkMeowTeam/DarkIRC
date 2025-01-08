package net.darkmeow.irc.config.configs

@Suppress("Unused")
class Config {
    /**
     * 服务器监听端口
     */
    var port: Int = 8080
    /**
     * 是否启用 Proxy Protocol
     */
    var proxyProtocol: Boolean = false
    /**
     * 服务器连接密钥
     */
    var key: String = "publicIRCTest123"
    /**
     * 存储用户数据的数据库
     */
    var database: String = "jdbc:sqlite:data.db"

    var userLimit: UserLimit = UserLimit()

    class UserLimit {
        /**
         * 是否允许多设备同时登录
         */
        var allowMultiDeviceLogin: Boolean = false
    }
}
