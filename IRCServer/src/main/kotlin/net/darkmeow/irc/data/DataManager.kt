package net.darkmeow.irc.data

import net.darkmeow.irc.IRCServer
import net.darkmeow.irc.network.packet.s2c.S2CPacketUpdateMyInfo.Premium
import java.sql.Connection
import java.sql.DriverManager

class DataManager(
    private val base: IRCServer
) {
    lateinit var connection: Connection

    fun connect(url: String) {
        connection = DriverManager.getConnection(url)

        connection.createStatement()
            .also {
                if (!it.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='clients';").next()) {
                    connection.createStatement().executeUpdate(
                        arrayOf(
                            "CREATE TABLE IF NOT EXISTS clients (",
                            "id TEXT PRIMARY KEY,",
                            "hash TEXT NOT NULL,",
                            "allow_login_min_version INTEGER",
                            ");"
                        ).joinToString("")
                    )
                    connection.createStatement().executeUpdate(
                        arrayOf(
                            "INSERT INTO clients (id, hash, allow_login_min_version) ",
                            "VALUES ('DarkMeow', '114514', 0);"
                        ).joinToString("")
                    )
                }
                if (!it.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='users';").next()) {
                    connection.createStatement().executeUpdate(
                        arrayOf(
                            "CREATE TABLE IF NOT EXISTS users (",
                            "name TEXT PRIMARY KEY,",
                            "password TEXT NOT NULL,",
                            "rank TEXT NOT NULL,", // 头衔
                            "premium INTEGER DEFAULT 0", // 0 -> 普通入 1 -> 管理入
                            ");"
                        ).joinToString("")
                    )
                    connection.createStatement().executeUpdate(
                        arrayOf(
                            "INSERT INTO users (name, password, rank, premium) ",
                            "VALUES ('NekoCurit', '114514', '小枫猫娘', ${Premium.SUPER_ADMIN.ordinal});"
                        ).joinToString("")
                    )
                }
            }
            .close()
    }

    fun disconnect() {
        runCatching {
            connection.close()
        }
    }

    fun getClientMinLoginVersion(id: String, hash: String): Int? = connection
        .prepareStatement("SELECT allow_login_min_version FROM clients WHERE id = ? AND hash = ?;")
        .apply {
            setString(1, id)
            setString(2, hash)
        }
        .executeQuery()
        .takeIf { it.next() }
        ?.getInt("allow_login_min_version")

    fun checkUserPassword(user: String, password: String): Boolean = connection
        .prepareStatement("SELECT name, rank, premium FROM users WHERE name = ? AND password = ?;")
        .apply {
            setString(1, user)
            setString(2, password)
        }
        .executeQuery()
        .next()

    fun getUserRank(user: String): String? = connection
        .prepareStatement("SELECT rank FROM users WHERE name = ?;")
        .apply {
            setString(1, user)
        }
        .executeQuery()
        .takeIf { it.next() }
        ?.getString("rank")

    fun getUserPremium(user: String): Premium? = connection
        .prepareStatement("SELECT premium FROM users WHERE name = ?;")
        .apply {
            setString(1, user)
        }
        .executeQuery()
        .takeIf { it.next() }
        ?.getInt("premium")
        ?.let { Premium.entries[it] } ?: Premium.GUEST
}