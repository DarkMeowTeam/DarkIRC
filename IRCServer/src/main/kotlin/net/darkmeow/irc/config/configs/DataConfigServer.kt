package net.darkmeow.irc.config.configs

import kotlinx.serialization.Serializable

@Serializable
data class DataConfigServer(
    /**
     * 服务器监听 IP
     */
    val host: String = "0.0.0.0",
    /**
     * 服务器监听端口
     */
    val port: Int = 45020,
    /**
     * 是否启用 Proxy Protocol
     */
    val proxyProtocol: Boolean = false,
    /**
     * 是否要求客户端进行签名认证
     */
    val signature: Boolean = false,
    /**
     * 是否启用协议加密
     */
    val encryption: Boolean = false,
    /**
     * 传输数据压缩功能
     */
    val compression: Compression = Compression()
) {
    @Serializable
    data class Compression(
        /**
         * 传输数据压缩功能开启状态
         * true: 启用
         * false: 禁用
         */
        val state: Boolean = false,
        /**
         * 传输数据压缩功能启用压缩最低包大小
         * 避免非常小的包被压缩导致反效果
         */
        val threshold: Int = 256
    )
}