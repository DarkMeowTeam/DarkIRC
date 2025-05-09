package net.darkmeow.irc.database.extensions

import net.darkmeow.irc.data.base.DataSession
import net.darkmeow.irc.database.DataBaseManager
import net.darkmeow.irc.database.data.DataBaseSession
import net.darkmeow.irc.database.exceptions.DataSessionNotFoundException
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

object DataManagerSessionExtensions {

    /**
     * 创建新的会话凭据
     *
     * @param metadata 会话数据
     *
     * @return 会话信息 包括 token 信息
     */
    fun DataBaseManager.createSession(metadata: DataSession.SessionMetadata): DataSession {
        val token = DataSession.generateToken()

        transaction(database) {
            DataBaseSession.insert {
                it[this.token] = token
                it[this.user] = metadata.user
                it[this.lastLoginTimestamp] = metadata.lastLoginTimestamp
                it[this.lastLoginHardWareUniqueId] = metadata.lastLoginHardWareUniqueId
                it[this.lastLoginIp] = metadata.lastLoginIp
            }
        }

        return DataSession(
            token = token,
            metadata = metadata
        )
    }

    /**
     * 更新会话凭据信息
     *
     * @param token 会话凭据
     * @param metadata 会话数据
     *
     * @throws DataSessionNotFoundException 会话不存在
     */
    fun DataBaseManager.updateSession(token: String, metadata: DataSession.SessionMetadata) {
        transaction(database) {
            DataBaseSession
                .update({ DataBaseSession.token eq token }) {
                    it[this.user] = metadata.user
                    it[this.lastLoginTimestamp] = metadata.lastLoginTimestamp
                    it[this.lastLoginHardWareUniqueId] = metadata.lastLoginHardWareUniqueId
                    it[this.lastLoginIp] = metadata.lastLoginIp
                }
                .also { count ->
                    if (count == 0) throw DataSessionNotFoundException(token)
                }
        }
    }

    /**
     * 删除会话凭据
     *
     * @param token 会话凭据
     *
     * @throws DataSessionNotFoundException 会话不存在
     */
    fun DataBaseManager.deleteSession(token: String) {
        transaction(database) {
            DataBaseSession
                .deleteWhere { DataBaseSession.token eq token }
                .also { count ->
                    if (count == 0) throw DataSessionNotFoundException(token)
                }
        }
    }


    /**
     * 删除一个用户下的所有会话凭据
     *
     * @param name 用户名
     *
     * @return 删除数量
     */
    fun DataBaseManager.deleteSessionByUser(name: String) = transaction(database) {
        DataBaseSession.deleteWhere { DataBaseSession.user eq name }
    }

    /**
     * 检查一个会话凭据是否存在
     *
     * @param token 会话凭据
     *
     * @return 存在状态
     */
    fun DataBaseManager.sessionExist(token: String) = transaction(database) {
        DataBaseSession
            .selectAll()
            .where(DataBaseSession.token eq token)
            .count() > 0
    }

    /**
     * 获取会话凭据信息
     *
     * @param token 会话凭据
     *
     * @throws DataSessionNotFoundException 会话不存在
     * @return 信息
     */
    fun DataBaseManager.getSessionMetadata(token: String) = transaction(database) {
        DataBaseSession
            .selectAll()
            .where(DataBaseSession.token eq token)
            .singleOrNull()
            ?.let {
                DataSession(
                    token = it[DataBaseSession.token],
                    metadata = DataSession.SessionMetadata(
                        user = it[DataBaseSession.user],
                        lastLoginTimestamp = it[DataBaseSession.lastLoginTimestamp],
                        lastLoginHardWareUniqueId = it[DataBaseSession.lastLoginHardWareUniqueId],
                        lastLoginIp = it[DataBaseSession.lastLoginIp]
                    )
                )
            }
            ?: throw DataSessionNotFoundException(token)
    }

    /**
     * 获取所有会话凭据信息
     *
     * @param user 如果不为空 则筛选指定用户名
     * @return 所有会话凭据信息
     */
    fun DataBaseManager.getSessions(user: String? = null) = transaction(database) {
        DataBaseSession
            .selectAll()
            .let { data ->
                user
                    ?.let { update ->
                        data.where(DataBaseSession.user eq user)
                    }
                    ?: data
            }
            .map {
                DataSession(
                    token = it[DataBaseSession.token],
                    metadata = DataSession.SessionMetadata(
                        user = it[DataBaseSession.user],
                        lastLoginTimestamp = it[DataBaseSession.lastLoginTimestamp],
                        lastLoginHardWareUniqueId = it[DataBaseSession.lastLoginHardWareUniqueId],
                        lastLoginIp = it[DataBaseSession.lastLoginIp]
                    )
                )
            }
            .toMutableSet()
    }
}