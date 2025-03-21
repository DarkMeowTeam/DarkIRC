package net.darkmeow.irc.config.configs

@Suppress("Unused")
class Config {

    /**
     * 存储用户数据的数据库
     */
    var database: String = "jdbc:sqlite:data.db"

    var userLimit: UserLimit = UserLimit()

    var ircServer = IRCServer()

    class IRCServer {
        /**
         * 服务器监听 IP
         */
        var host = "0.0.0.0"
        /**
         * 服务器监听端口
         */
        var port = 8080
        /**
         * 是否启用 Proxy Protocol
         */
        var proxyProtocol: Boolean = false
        /**
         * 服务器连接密钥
         */
        var key: String = "publicIRCTest123"
    }

    class UserLimit {
        /**
         * 是否允许多设备同时登录
         */
        var allowMultiDeviceLogin: Boolean = false
    }

    var webServer: WebServer = WebServer()

    class WebServer {
        /**
         * Web API 端口
         */
        var port = 8888
        /**
         * Web API 密钥
         */
        var key = ""
        /**
         * Web API IP 白名单
         * 留空关闭
         */
        var ipWhiteList = mutableSetOf<String>()
    }
}
