package net.darkmeow.irc.data.base

import net.darkmeow.irc.data.enmus.EnumUserPremium

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
        val password: String
    )
}