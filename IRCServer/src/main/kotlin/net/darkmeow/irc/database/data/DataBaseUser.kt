package net.darkmeow.irc.database.data

import org.jetbrains.exposed.sql.Table

object DataBaseUser : Table("users") {
    val name = text("name")
    val password = text("password")
    val premium = integer("premium")

    override val primaryKey = PrimaryKey(name)
}
