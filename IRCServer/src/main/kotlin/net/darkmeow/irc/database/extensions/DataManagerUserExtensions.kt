package net.darkmeow.irc.database.extensions

import net.darkmeow.irc.data.base.DataUser
import net.darkmeow.irc.data.enmus.EnumUserPremium
import net.darkmeow.irc.database.DataBaseManager
import net.darkmeow.irc.database.data.DataBaseUser
import net.darkmeow.irc.database.exceptions.DataClientNotFoundException
import net.darkmeow.irc.database.exceptions.DataUserAlreadyExistException
import net.darkmeow.irc.database.exceptions.DataUserNotFoundException
import net.darkmeow.irc.utils.kotlin.ObjectUtils
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.mindrot.jbcrypt.BCrypt

object DataManagerUserExtensions {
    /**
     * 创建用户
     *
     * @param name 用户名
     * @param password 初始密码
     * @param premium 用户等级
     */
    fun DataBaseManager.createUser(name: String, password: String, premium: EnumUserPremium): DataUser {
        val passwordHash = BCrypt.hashpw(password, BCrypt.gensalt())

        transaction(database) {
            if (DataBaseUser.selectAll().any { it[DataBaseUser.name] == name }) {
                throw DataUserAlreadyExistException(name)
            }

            DataBaseUser.insert {
                it[this.name] = name
                it[this.password] = passwordHash
                it[this.premium] = premium.ordinal
            }
        }

        return DataUser(
            name = name,
            metadata = DataUser.UserMetadata(
                premium = premium,
                passwordHash = passwordHash
            )
        )
    }

    /**
     * 删除用户
     *
     * @param name 用户名
     *
     * @throws DataUserNotFoundException 用户名不存在
     */
    fun DataBaseManager.deleteUser(name: String) {
        transaction(database) {
            DataBaseUser
                .deleteWhere { DataBaseUser.name eq name }
                .also { count ->
                    if (count == 0) throw DataUserNotFoundException(name)
                }
        }
    }

    /**
     * 检查一个用户是否存在
     *
     * @param name 用户名
     * @return 存在状态
     */
    fun DataBaseManager.userExist(name: String) = transaction(database) {
        DataBaseUser
            .selectAll()
            .where(DataBaseUser.name eq name)
            .count() > 0
    }

    /**
     * 获取用户信息
     *
     * @param name 用户名
     *
     * @return 信息
     * @throws DataUserNotFoundException 用户名不存在
     */
    fun DataBaseManager.getUserMetadata(name: String) = transaction(database) {
        DataBaseUser
            .selectAll()
            .where(DataBaseUser.name eq name)
            .singleOrNull()
            ?.let {
                DataUser(
                    name = it[DataBaseUser.name],
                    metadata = DataUser.UserMetadata(
                        premium = EnumUserPremium.entries[it[DataBaseUser.premium]],
                        passwordHash = it[DataBaseUser.password]
                    )
                )
            }
            ?: throw DataUserNotFoundException(name)
    }

    /**
     * 获取所有用户信息
     *
     * @return 所有用户信息
     */
    fun DataBaseManager.getUsers() = transaction(database) {
        DataBaseUser
            .selectAll()
            .map {
                DataUser(
                    name = it[DataBaseUser.name],
                    metadata = DataUser.UserMetadata(
                        premium = EnumUserPremium.entries[it[DataBaseUser.premium]],
                        passwordHash = it[DataBaseUser.password]
                    )
                )
            }
            .toMutableSet()
    }

    /**
     * 更新用户信息
     *
     * @param name 用户名
     * @param premium 非空则更新权限信息
     * @param password 非空则更新密码信息
     * @throws IllegalArgumentException 提供更新参数全部为空
     *
     * @throws DataClientNotFoundException 客户端不存在在
     * @throws IllegalArgumentException 提供更新参数全部为空
     */
    @JvmOverloads
    fun DataBaseManager.updateUserMetadata(name: String, premium: EnumUserPremium? = null, password: String? = null) {
        if (ObjectUtils.allNull(premium, password)) throw IllegalArgumentException("未提供任何更新参数")

        transaction(database) {
            DataBaseUser
                .update({ DataBaseUser.name eq name }) {
                    premium?.also { update ->
                        it[this.premium] = update.ordinal
                    }
                    password?.also { update ->
                        it[this.password] = BCrypt.hashpw(update, BCrypt.gensalt())
                    }
                }
                .also { count ->
                    if (count == 0) throw DataClientNotFoundException(name)
                }
        }
    }
}