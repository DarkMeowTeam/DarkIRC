package net.darkmeow.irc.utils.userdata

import net.darkmeow.irc.database.DataBaseManager
import net.darkmeow.irc.database.extensions.DataManagerUserdataExtensions.getUserdata
import net.darkmeow.irc.database.extensions.DataManagerUserdataExtensions.setUserdata

object UserdataIgnoreUtils {

    /**
     * 读取用户已屏蔽列表
     *
     * @param name 用户名
     *
     * @return 屏蔽列表
     */
    fun DataBaseManager.getUserIgnores(name: String) = getUserdata(name, "ignores")
        ?.split(",")
        ?.toMutableSet()
        ?: mutableSetOf()


    /**
     * 设置用户已屏蔽列表 将会覆盖旧的数据
     *
     * @param name 用户名
     * @param ignores 屏蔽列表
     */
    fun DataBaseManager.setUserIgnores(name: String, ignores: Set<String>) {
        setUserdata(name, "ignores", ignores.joinToString(separator = ","))
    }

}