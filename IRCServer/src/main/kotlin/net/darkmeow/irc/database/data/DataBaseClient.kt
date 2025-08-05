package net.darkmeow.irc.database.data

import org.jetbrains.exposed.sql.Table

object DataBaseClient : Table("clients") {
    val id = text("id")
    val keyPublic = binary("key_public", 1024)
    val keyPrivate = binary("key_private", 1024)
    val allowLoginMinVersion = integer("allow_login_min_version")

    override val primaryKey = PrimaryKey(id)
}
