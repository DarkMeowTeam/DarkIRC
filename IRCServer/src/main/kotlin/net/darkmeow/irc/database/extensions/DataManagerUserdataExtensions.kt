package net.darkmeow.irc.database.extensions

import net.darkmeow.irc.database.DataBaseManager
import net.darkmeow.irc.database.data.DataBaseUserdata
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

object DataManagerUserdataExtensions {

    /**
     * 读取用户自定义数据
     *
     * @param name 用户名
     * @param key 键
     *
     * @return 数据 如果不存在则返回空
     */
    fun DataBaseManager.getUserdata(name: String, key: String) = transaction(database) {
        DataBaseUserdata
            .selectAll()
            .where(DataBaseUserdata.name eq "${name}-${key}")
            .singleOrNull()
            ?.let { it[DataBaseUserdata.value] }
    }

    /**
     * 写入用户自定义数据
     *
     * @param name 用户名
     * @param key 键
     * @param value 值
     */
    fun DataBaseManager.setUserdata(name: String, key: String, value: String) {
        transaction(database) {
            DataBaseUserdata
                .update({ DataBaseUserdata.name eq "$name-$key" }) {
                    it[DataBaseUserdata.value] = value
                }
                .takeIf { it == 0 }
                ?.also {
                    DataBaseUserdata.insert {
                        it[this.name] = "$name-$key"
                        it[this.value] = value
                    }
                }
        }
    }

    /**
     * 删除用户自定义数据
     *
     * @param name 用户名
     * @param key 键 如果不提供则删除指定用户所有数据
     */
    fun DataBaseManager.deleteUserdata(name: String, key: String? = null) {
        transaction(database) {
            DataBaseUserdata.deleteWhere {
                key
                    ?.let { update ->
                        DataBaseUserdata.name eq "$name-$update"
                    }
                    ?: let {
                        DataBaseUserdata.name like "$name-%"
                    }
            }
        }
    }

    /**
     * 用户自定义数据是否存在
     *
     * @param name 用户名
     * @param key 键
     *
     * @return 存在状态
     */
    fun DataBaseManager.userdataExist(name: String, key: String) = transaction(database) {
        DataBaseUserdata
            .selectAll()
            .where(DataBaseUserdata.name eq "$name-$key")
            .count() > 0
    }

}