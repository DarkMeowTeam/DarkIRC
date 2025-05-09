package net.darkmeow.irc.data.base

/**
 * 会话凭据数据
 *
 * @param token 会话凭据 不可变动项
 * @param metadata 元素据
 */
data class DataSession(
    val token: String,
    val metadata: SessionMetadata,
) {
    data class SessionMetadata(
        val user: String,
        val lastLoginTimestamp: Long,
        val lastLoginHardWareUniqueId: String,
        val lastLoginIp: String
    )

    companion object {
        /**
         * 生成会话凭据 token 段
         */
        @Suppress("SpellCheckingInspection")
        fun generateToken() = (1..128)
            .map { "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".random() }
            .joinToString("")

        /**
         * 指定字符串是否为会话凭据 token 段
         */
        fun String.isToken() = this.length == 128
    }
}