package net.darkmeow.irc.database.data

import org.jetbrains.exposed.sql.Table

object DataBaseUserdata : Table("userdata") {
    val name = text("name")
    val value = text("value")

    override val primaryKey = PrimaryKey(name)
}
