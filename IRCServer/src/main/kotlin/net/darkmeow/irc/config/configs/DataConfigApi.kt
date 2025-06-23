package net.darkmeow.irc.config.configs

import kotlinx.serialization.Serializable

@Serializable
data class DataConfigApi(
    /**
     * Web API 监听 IP
     */
    val host: String = "0.0.0.0",
    /**
     * Web API 端口
     */
    val port: Int = 45021,
    /**
     * Web API 密钥
     * 配置后请添加额外的header "Authorization: Basic key"
     * 留空关闭
     */
    val key: String = "",
    /**
     * Web API IP 白名单
     * 留空关闭
     */
    val ipWhiteList: MutableSet<String> = mutableSetOf()
)