package net.darkmeow.irc.data.base

/**
 * 客户端数据
 *
 * @param name 客户端名称 不可变动项
 * @param key 客户端登录密钥
 * @param metadata 元数据
 */
data class DataClient(
    val name: String,
    val key: String,
    val metadata: ClientMetadata,
) {
    data class ClientMetadata(
        val allowLoginMinVersion: Int,
        val clientAdministrators: MutableSet<String> = mutableSetOf(),
        val clientUsers: MutableSet<String> = mutableSetOf()
    )

    companion object {
        @Suppress("SpellCheckingInspection")
        fun generateKey() = (1..32)
            .map { "abcdefghijklmnopqrstuvwxyz0123456789".random() }
            .joinToString("")
    }
}