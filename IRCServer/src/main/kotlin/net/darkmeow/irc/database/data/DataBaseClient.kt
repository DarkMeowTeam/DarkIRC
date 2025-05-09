package net.darkmeow.irc.database.data

import org.jetbrains.exposed.sql.Table

object DataBaseClient : Table("clients") {
    val id = text("id")
    val hash = text("hash")
    val allowLoginMinVersion = integer("allow_login_min_version")
    val usersAllowLogin = text("users_allow_login")
    val usersClientAdministrator = text("users_client_administrator")

    override val primaryKey = PrimaryKey(id)
}
