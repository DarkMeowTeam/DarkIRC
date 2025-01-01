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

    fun clientExist(id: String): Boolean = connection
        .prepareStatement("SELECT 1 FROM clients WHERE id = ?;")
        .apply {
            setString(1, id)
        }
        .executeQuery()
        .next()

    fun getClientMinLoginVersion(id: String, hash: String): Int? = connection
        .prepareStatement("SELECT allow_login_min_version FROM clients WHERE id = ? AND hash = ?;")
        .apply {
            setString(1, id)
            setString(2, hash)
        }
        .executeQuery()
        .takeIf { it.next() }
        ?.getInt("allow_login_min_version")

    fun createClient(id: String, hash: String, allowLoginMinVersion: Int): Boolean = connection
        .prepareStatement(
            arrayOf(
                "INSERT INTO clients (id, hash, allow_login_min_version) ",
                "VALUES (?, ?, ?);"
            ).joinToString("")
        ).apply {
            setString(1, id)
            setString(2, hash)
            setInt(3, allowLoginMinVersion)
        }.executeUpdate() > 0

    fun deleteClient(id: String): Boolean =
        connection.prepareStatement(
            arrayOf(
                "DELETE FROM clients ",
                "WHERE id = ?;"
            ).joinToString("")
        ).apply {
            setString(1, id)
        }.executeUpdate() > 0

    fun setClientMinVersion(id: String, newMinVersion: Int): Boolean = connection
        .prepareStatement(
            arrayOf(
                "UPDATE clients  ",
                "SET allow_login_min_version = ? ",
                "WHERE id = ?;"
            ).joinToString("")
        ).apply {
            setInt(1, newMinVersion)
            setString(2, id)
        }.executeUpdate() > 0

    fun checkUserPassword(user: String, password: String): Boolean = connection
        .prepareStatement("SELECT name, rank, premium FROM users WHERE name = ? AND password = ?;")
        .apply {
            setString(1, user)
            setString(2, password)
        }
        .executeQuery()
        .next()

    fun setUserPassword(user: String, newPassword: String): Boolean = connection
        .prepareStatement("UPDATE users SET password = ? WHERE name = ?;")
        .apply {
            setString(1, newPassword)
            setString(2, user)
        }
        .executeUpdate() > 0

    fun createUser(name: String, password: String, rank: String, premium: Premium): Boolean = connection
        .prepareStatement(
            arrayOf(
                "INSERT INTO users (name, password, rank, premium) ",
                "VALUES (?, ?, ?, ?);"
            ).joinToString("")
        ).apply {
            setString(1, name)
            setString(2, password)
            setString(3, rank)
            setInt(4, premium.ordinal)
        }
        .executeUpdate() > 0

    fun deleteUser(name: String): Boolean = connection
        .prepareStatement(
            arrayOf(
                "DELETE FROM users ",
                "WHERE name = ?;"
            ).joinToString("")
        ).apply {
            setString(1, name)
        }
        .executeUpdate() > 0

    fun userExist(name: String): Boolean = connection
        .prepareStatement("SELECT 1 FROM users WHERE name = ?;")
        .apply {
            setString(1, name)
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

    fun setUserRank(user: String, newRank: String): Boolean = connection
        .prepareStatement("UPDATE users SET rank = ? WHERE name = ?;")
        .apply {
            setString(1, newRank)
            setString(2, user)
        }
        .executeUpdate() > 0

    fun getUserPremium(user: String): Premium = connection
        .prepareStatement("SELECT premium FROM users WHERE name = ?;")
        .apply {
            setString(1, user)
        }
        .executeQuery()
        .takeIf { it.next() }
        ?.getInt("premium")
        ?.let { Premium.entries[it] } ?: Premium.GUEST

    fun setUserPremium(user: String, newPremium: Premium): Boolean = connection
        .prepareStatement("UPDATE users SET premium = ? WHERE name = ?;")
        .apply {
            setInt(1, newPremium.ordinal)
            setString(2, user)
        }
        .executeUpdate() > 0

}