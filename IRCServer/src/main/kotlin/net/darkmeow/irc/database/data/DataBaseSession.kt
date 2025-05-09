package net.darkmeow.irc.database.data

import org.jetbrains.exposed.sql.Table

object DataBaseSession : Table("sessions") {
    val token = text("token")
    val user = text("user")
    val lastLoginTimestamp = long("last_login_timestamp")
    val lastLoginHardWareUniqueId = text("last_login_hard_ware_unique_id")
    val lastLoginIp = text("last_login_ip")

    override val primaryKey = PrimaryKey(token)
}
