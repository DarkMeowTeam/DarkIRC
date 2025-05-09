package net.darkmeow.irc.database

import net.darkmeow.irc.IRCServer
import net.darkmeow.irc.database.data.DataBaseClient
import net.darkmeow.irc.database.data.DataBaseSession
import net.darkmeow.irc.database.data.DataBaseUser
import net.darkmeow.irc.database.data.DataBaseUserdata
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

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

        base.logger.info("[数据库管理] 初始化成功.")
    }

}