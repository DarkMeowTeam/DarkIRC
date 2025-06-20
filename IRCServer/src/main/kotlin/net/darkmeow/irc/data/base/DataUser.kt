package net.darkmeow.irc.data.base

import net.darkmeow.irc.data.enmus.EnumUserPremium
import org.mindrot.jbcrypt.BCrypt

/**
 * 用户数据
 *
 * @param name 用户名
 * @param metadata 元素据
 */
data class DataUser(
    val name: String,
    val metadata: UserMetadata,
) {
    data class UserMetadata(
        val premium: EnumUserPremium,
        private val passwordHash: String
    ) {
        /**
         * 检查密码是否正确
         *
         * @param password 客户端传输过来的密码
         */
        fun checkPassword(password: String) = BCrypt.checkpw(password, passwordHash)
    }
}