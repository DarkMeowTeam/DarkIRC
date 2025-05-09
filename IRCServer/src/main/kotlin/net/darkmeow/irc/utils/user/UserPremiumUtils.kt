package net.darkmeow.irc.utils.user

import net.darkmeow.irc.data.enmus.EnumUserPremium
import net.darkmeow.irc.database.DataBaseManager
import net.darkmeow.irc.database.extensions.DataManagerClientExtensions.getClientMetadata
import net.darkmeow.irc.database.extensions.DataManagerUserExtensions.getUserMetadata
import net.darkmeow.irc.database.extensions.DataManagerUserdataExtensions.getUserdata
import net.darkmeow.irc.database.extensions.DataManagerUserdataExtensions.setUserdata

object UserPremiumUtils {

    /**
     * 用户是否有指定客户端管理员权限
     *
     * @param client 客户端名
     * @param name 用户名
     *
     * @return 是否有管理员权限
     */
    fun DataBaseManager.isClientAdmin(client: String, name: String): Boolean {
        val userMeta = getUserMetadata(name)
        val clientMeta = getClientMetadata(client)

        return userMeta.metadata.premium.ordinal >= EnumUserPremium.OWNER.ordinal || clientMeta.metadata.clientAdministrators.contains(name)
    }

}