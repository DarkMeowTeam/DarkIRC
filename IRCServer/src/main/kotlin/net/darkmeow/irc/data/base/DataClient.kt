package net.darkmeow.irc.data.base

import java.security.KeyPair

/**
 * 客户端数据
 *
 * @param name 客户端名称 不可变动项
 * @param key 客户端登录密钥
 * @param metadata 元数据
 */
data class DataClient(
    val name: String,
    val key: KeyPair,
    val metadata: ClientMetadata,
) {
    data class ClientMetadata(
        val allowLoginMinVersion: Int
    )
}