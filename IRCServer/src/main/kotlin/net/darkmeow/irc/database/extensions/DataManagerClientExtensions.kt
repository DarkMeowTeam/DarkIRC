package net.darkmeow.irc.database.extensions

import net.darkmeow.irc.data.base.DataClient
import net.darkmeow.irc.database.DataBaseManager
import net.darkmeow.irc.database.data.DataBaseClient
import net.darkmeow.irc.database.exceptions.DataClientAlreadyExistException
import net.darkmeow.irc.database.exceptions.DataClientNotFoundException
import net.darkmeow.irc.utils.CryptUtils
import net.darkmeow.irc.utils.kotlin.ObjectUtils
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.security.KeyPair

object DataManagerClientExtensions {

    /**
     * 创建新的客户端
     *
     * @param name 客户端名称
     * @param metadata 客户端数据
     */
    fun DataBaseManager.createClient(name: String, metadata: DataClient.ClientMetadata): DataClient {
        val key = CryptUtils.generateKeyPair()

        transaction(database) {
            if (DataBaseClient.selectAll().any { it[DataBaseClient.id] == name }) {
                throw DataClientAlreadyExistException(name)
            }
            DataBaseClient.insert {
                it[this.id] = name
                it[this.keyPublic] = key.public.encoded
                it[this.keyPrivate] = key.private.encoded
                it[this.allowLoginMinVersion] = metadata.allowLoginMinVersion
            }
        }

        return DataClient(
            name = name,
            key = key,
            metadata = metadata,
        )
    }

    /**
     * 删除客户端
     *
     * @param name 客户端名称
     *
     * @throws DataClientNotFoundException 客户端不存在
     */
    fun DataBaseManager.deleteClient(name: String) {
        transaction(database) {
            DataBaseClient
                .deleteWhere { DataBaseClient.id eq name }
                .also { count ->
                    if (count == 0) throw DataClientNotFoundException(name)
                }
        }
    }

    /**
     * 检查一个客户端是否存在
     *
     * @param name 客户端名称
     *
     * @return 存在状态
     */
    fun DataBaseManager.clientExist(name: String) = transaction(database) {
        DataBaseClient
            .selectAll()
            .where(DataBaseClient.id eq name)
            .count() > 0
    }

    /**
     * 获取客户端信息
     *
     * @param name 客户端名称
     * @return 信息
     *
     * @throws DataClientNotFoundException 客户端不存在
     */
    fun DataBaseManager.getClientMetadata(name: String) = transaction(database) {
        DataBaseClient
            .selectAll()
            .where(DataBaseClient.id eq name)
            .singleOrNull()
            ?.let {
                DataClient(
                    name = it[DataBaseClient.id],
                    key = KeyPair(
                        CryptUtils.loadPublicKeyFromByte(it[DataBaseClient.keyPublic]),
                        CryptUtils.loadPrivateKeyFromByte(it[DataBaseClient.keyPrivate])
                    ),
                    metadata = DataClient.ClientMetadata(
                        allowLoginMinVersion = it[DataBaseClient.allowLoginMinVersion]
                    )
                )
            }
            ?: throw DataClientNotFoundException(name)
    }

    /**
     * 获取所有客户端信息
     *
     * @return 所有客户端信息
     */
    fun DataBaseManager.getClients() = transaction(database) {
        DataBaseClient
            .selectAll()
            .map {
                DataClient(
                    name = it[DataBaseClient.id],
                    key = KeyPair(
                        CryptUtils.loadPublicKeyFromByte(it[DataBaseClient.keyPublic]),
                        CryptUtils.loadPrivateKeyFromByte(it[DataBaseClient.keyPrivate])
                    ),
                    metadata = DataClient.ClientMetadata(
                        allowLoginMinVersion = it[DataBaseClient.allowLoginMinVersion],
                    )
                )
            }
            .toMutableSet()
    }

    /**
     * 更新客户端信息
     *
     * @param name 客户端名称
     * @param key 非空则更新客户端密钥
     * @param allowLoginMinVersion 非空则更新客户端最低允许登录版本
     *
     * @throws DataClientNotFoundException 客户端不存在在
     * @throws IllegalArgumentException 提供更新参数全部为空
     */
    @JvmOverloads
    fun DataBaseManager.updateClientMetadata(name: String, key: KeyPair? = null, allowLoginMinVersion: Int? = null) {
        if (ObjectUtils.allNull(key, allowLoginMinVersion)) throw IllegalArgumentException("未提供任何更新参数")

        transaction(database) {
            DataBaseClient
                .update({ DataBaseClient.id eq name }) {
                    key?.also { update ->
                        it[this.keyPublic] = key.public.encoded
                        it[this.keyPrivate] = key.private.encoded
                    }
                    allowLoginMinVersion?.also { update ->
                        it[this.allowLoginMinVersion] = update
                    }
                }
                .also { count ->
                    if (count == 0 ) throw DataClientNotFoundException(name)
                }
        }
    }
}