package net.darkmeow.irc.database

import net.darkmeow.irc.IRCServer
import net.darkmeow.irc.data.base.DataClient
import net.darkmeow.irc.data.enmus.EnumUserPremium
import net.darkmeow.irc.database.data.DataBaseClient
import net.darkmeow.irc.database.data.DataBaseSession
import net.darkmeow.irc.database.data.DataBaseUser
import net.darkmeow.irc.database.data.DataBaseUserdata
import net.darkmeow.irc.database.extensions.DataManagerClientExtensions.createClient
import net.darkmeow.irc.database.extensions.DataManagerClientExtensions.getClients
import net.darkmeow.irc.database.extensions.DataManagerUserExtensions.createUser
import net.darkmeow.irc.database.extensions.DataManagerUserExtensions.getUsers
import net.darkmeow.irc.utils.CryptUtils
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.Base64

class DataBaseManager(
    private val base: IRCServer
) {
    lateinit var database: Database

    fun connect() {
        base.logger.info("[数据库管理] 正在初始化..")

        database = Database.connect(
            url = base.configManager.configs.database.url,
            user = base.configManager.configs.database.user,
            driver = base.configManager.configs.database.driver,
            password = base.configManager.configs.database.password
        )

        transaction(database) {
            SchemaUtils.create(DataBaseUser, DataBaseUserdata, DataBaseSession, DataBaseClient)
        }

        createDefault()
        base.logger.info("[数据库管理] 初始化成功.")
    }

    fun createDefault() {
        if (this.getClients().isEmpty() && this.getUsers().isEmpty()) {
            this.createClient(name = "default", metadata = DataClient.ClientMetadata(allowLoginMinVersion = 0)).also { client ->
                base.logger.info(
                    StringBuilder()
                        .apply {
                            appendLine("[数据库管理] 创建默认登录客户端信息")
                            appendLine("  name: ${client.name}")
                            appendLine("  key:")
                            Base64
                                .getMimeEncoder(64, byteArrayOf('\n'.code.toByte()))
                                .encodeToString(client.key.private.encoded)
                                .split("\n")
                                .forEach { text ->
                                    appendLine("    $text")
                                }
                        }
                        .toString()
                )
            }
            this.createUser(name = "admin", password = "123456", premium = EnumUserPremium.OWNER).also {
                base.logger.info("[数据库管理] 创建默认用户(name: admin, password: 123456)")
            }
        }
    }

}