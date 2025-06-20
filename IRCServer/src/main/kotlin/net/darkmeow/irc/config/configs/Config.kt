package net.darkmeow.irc.config.configs

class Config {

    /**
     * 存储用户数据的数据库
     */
    var database = DataBase()

    class DataBase {
        var url: String = "jdbc:sqlite:data.db"
        var driver: String = "org.sqlite.JDBC"
        var user: String = ""
        var password: String = ""
    }

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
        var port = 45020
        /**
         * 是否启用 Proxy Protocol
         */
        var proxyProtocol: Boolean = false
        /**
         * 是否要求客户端进行签名认证
         */
        var signature: Boolean = false
        /**
         * 是否启用协议加密
         */
        var encryption: Boolean = false
        /**
         * 传输数据压缩功能
         */
        var compression = Compression()

        class Compression {
            /**
             * 传输数据压缩功能开启状态
             * true: 启用
             * false: 禁用
             */
            var state: Boolean = false
            /**
             * 传输数据压缩功能启用压缩最低包大小
             * 避免非常小的包被压缩导致反效果
             */
            var threshold: Int = 256
        }
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
         * Web API 监听 IP
         */
        var host = "0.0.0.0"
        /**
         * Web API 端口
         */
        var port = 45021
        /**
         * Web API 密钥
         * 配置后请添加额外的header "Authorization: Basic ${Base64.getEncoder().encode("任意字符:key")}"
         */
        var key = ""
    }
}
