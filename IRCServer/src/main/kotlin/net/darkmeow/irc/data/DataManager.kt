package net.darkmeow.irc.data

import net.darkmeow.irc.IRCServer
import net.darkmeow.irc.data.enmus.EnumUserPremium
import org.apache.logging.log4j.LogManager
import java.sql.Connection
import java.sql.DriverManager

class DataManager(
    private val base: IRCServer
) {
    val logger = LogManager.getLogger("DataManager")
    lateinit var connection: Connection

    fun connect(url: String) {
        logger.info("[数据库管理] 正在连接数据库..")
        connection = DriverManager.getConnection(url)

        connection.createStatement()
            .also {
                if (!it.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='clients';").next()) {
                    connection.createStatement().executeUpdate(
                        """
                            CREATE TABLE IF NOT EXISTS clients (
                                id TEXT PRIMARY KEY,
                                hash TEXT NOT NULL,
                                allow_login_min_version INTEGER,
                                users_allow_login TEXT NOT NULL, -- 允许登录
                                users_client_administrator TEXT NOT NULL -- 客户端管理员
                            );
                        """.trimIndent()
                    )
                    connection.createStatement().executeUpdate(
                        """
                            INSERT INTO clients (id, hash, allow_login_min_version, users_allow_login, users_client_administrator) 
                            VALUES ('DarkMeow', '114514', 0, '', '');
                        """.trimIndent()
                    )

                    logger.info("[数据库管理] 创建表 clients")
                }
                if (!it.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='users';").next()) {
                    connection.createStatement().executeUpdate(
                        """
                            CREATE TABLE IF NOT EXISTS users (
                                name TEXT PRIMARY KEY,
                                password TEXT NOT NULL,
                                rank TEXT NOT NULL, -- 头衔
                                premium INTEGER DEFAULT 0 -- 0 -> 普通入 1 -> 管理入
                            );
                        """.trimIndent()
                    )
                    connection.createStatement().executeUpdate(
                        """
                            INSERT INTO users (name, password, rank, premium) 
                            VALUES ('Administrator', '123456', '管理员', ${EnumUserPremium.OWNER.ordinal});
                        """.trimIndent()
                    )

                    logger.info("[数据库管理] 创建表 users")
                }
                if (!it.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='sessions';").next()) {
                    connection.createStatement().executeUpdate(
                        """
                            CREATE TABLE IF NOT EXISTS sessions (
                                token TEXT PRIMARY KEY,
                                linkUser TEXT NOT NULL,
                                latestLogin INTEGER NOT NULL,
                                device INTEGER NOT NULL,
                                ip INTEGER NOT NULL
                            );
                        """.trimIndent()
                    )

                    logger.info("[数据库管理] 创建表 sessions")
                }
                if (!it.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='userdata';").next()) {
                    connection.createStatement().executeUpdate(
                        """
                            CREATE TABLE IF NOT EXISTS userdata (
                                name TEXT PRIMARY KEY,
                                ignores TEXT NOT NULL -- 屏蔽用户列表
                            );
                        """.trimIndent()
                    )

                    logger.info("[数据库管理] 创建表 userdata")
                }
            }
            .close()

        logger.info("[数据库管理] 数据库连接成功")
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

    fun getClientUsers(id: String): MutableSet<String>? = connection
        .prepareStatement("SELECT users_allow_login FROM clients WHERE id = ?;")
        .apply {
            setString(1, id)
        }
        .executeQuery()
        .takeIf { it.next() }
        ?.getString("users_allow_login")
        ?.split(",")
        ?.filter { it.isNotEmpty() }
        ?.toMutableSet()

    fun setClientUsers(id: String, users: Set<String>): Boolean = connection
        .prepareStatement(
            """
                UPDATE clients 
                SET users_allow_login = ? 
                WHERE id = ?;
            """.trimIndent()
        ).apply {
            setString(1, users.joinToString(","))
            setString(2, id)
        }.executeUpdate() > 0

    fun getClientAdministrators(id: String): MutableSet<String>? = connection
        .prepareStatement("SELECT users_client_administrator FROM clients WHERE id = ?;")
        .apply {
            setString(1, id)
        }
        .executeQuery()
        .takeIf { it.next() }
        ?.getString("users_client_administrator")
        ?.split(",")
        ?.filter { it.isNotEmpty() }
        ?.toMutableSet()

    fun setClientAdministrators(id: String, administrators: Set<String>): Boolean = connection
        .prepareStatement(
            """
                UPDATE clients 
                SET users_client_administrator = ? 
                WHERE id = ?;
            """.trimIndent()
        ).apply {
            setString(1, administrators.joinToString(","))
            setString(2, id)
        }.executeUpdate() > 0

    fun createClient(id: String, hash: String, allowLoginMinVersion: Int): Boolean = connection
        .prepareStatement(
            """
                INSERT INTO clients (id, hash, allow_login_min_version, users_allow_login, users_client_administrator) 
                VALUES (?, ?, ?, '', '');
            """.trimIndent()
        ).apply {
            setString(1, id)
            setString(2, hash)
            setInt(3, allowLoginMinVersion)
        }.executeUpdate() > 0

    fun deleteClient(id: String): Boolean =
        connection.prepareStatement(
            """
                DELETE FROM clients 
                WHERE id = ?;
            """.trimIndent()
        ).apply {
            setString(1, id)
        }.executeUpdate() > 0

    fun getClients(): MutableList<String> = mutableListOf<String>()
        .apply {
            connection.prepareStatement("SELECT id FROM clients;")
                .executeQuery()
                .use { resultSet ->
                    while (resultSet.next()) {
                        add(resultSet.getString("id"))
                    }
                }
        }

    fun setClientMinVersion(id: String, newMinVersion: Int): Boolean = connection
        .prepareStatement(
            """
                UPDATE clients 
                SET allow_login_min_version = ? 
                WHERE id = ?;
            """.trimIndent()
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

    fun getUsers(): MutableList<String> = mutableListOf<String>()
        .apply {
            connection.prepareStatement("SELECT name FROM users;")
                .executeQuery()
                .use { resultSet ->
                    while (resultSet.next()) {
                        add(resultSet.getString("name"))
                    }
                }
        }

    fun createUser(name: String, password: String, rank: String, premium: EnumUserPremium): Boolean = connection
        .prepareStatement(
            """
                INSERT INTO users (name, password, rank, premium) 
                VALUES (?, ?, ?, ?);
            """.trimIndent()
        ).apply {
            setString(1, name)
            setString(2, password)
            setString(3, rank)
            setInt(4, premium.ordinal)
        }
        .executeUpdate() > 0

    fun deleteUser(name: String): Boolean = connection
        .prepareStatement(
            """
                DELETE FROM users 
                WHERE name = ?;
            """.trimIndent()
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

    fun getUserPremium(user: String): EnumUserPremium = connection
        .prepareStatement("SELECT premium FROM users WHERE name = ?;")
        .apply {
            setString(1, user)
        }
        .executeQuery()
        .takeIf { it.next() }
        ?.getInt("premium")
        ?.let { EnumUserPremium.entries[it] } ?: EnumUserPremium.USER

    fun setUserPremium(user: String, newPremium: EnumUserPremium): Boolean = connection
        .prepareStatement("UPDATE users SET premium = ? WHERE name = ?;")
        .apply {
            setInt(1, newPremium.ordinal)
            setString(2, user)
        }
        .executeUpdate() > 0

    fun sessionExist(token: String): Boolean = connection
        .prepareStatement("SELECT 1 FROM sessions WHERE token = ?;")
        .apply {
            setString(1, token)
        }
        .executeQuery()
        .next()

    fun getSessionLinkUser(token: String): String? = connection
        .prepareStatement("SELECT linkUser FROM sessions WHERE token = ?;")
        .apply {
            setString(1, token)
        }
        .executeQuery()
        .takeIf { it.next() }
        ?.getString("linkUser")

    fun getSessionLastLogin(token: String): Long = connection
        .prepareStatement("SELECT latestLogin FROM sessions WHERE token = ?;")
        .apply {
            setString(1, token)
        }
        .executeQuery()
        .takeIf { it.next() }
        ?.getLong("latestLogin")
        ?: 0L

    fun updateSessionInfo(token: String, latestLogin: Long, device: String, ip: String): Boolean = connection
        .prepareStatement("UPDATE sessions SET latestLogin = ?, device = ?, ip = ? WHERE token = ?;")
        .apply {
            setLong(1, latestLogin)
            setString(2, device)
            setString(3, ip)
            setString(4, token)
        }
        .executeUpdate() > 0

    fun createSession(token: String, linkUser: String, latestLogin: Long, device: String, ip: String): Boolean = connection
        .prepareStatement(
            """
                INSERT INTO sessions (token, linkUser, latestLogin, device, ip) 
                VALUES (?, ?, ?, ?, ?);
            """.trimIndent()
        ).apply {
            setString(1, token)
            setString(2, linkUser)
            setLong(3, latestLogin)
            setString(4, device)
            setString(5, ip)
        }
        .executeUpdate() > 0

    fun deleteSession(token: String): Boolean = connection
        .prepareStatement(
            """
                DELETE FROM sessions
                WHERE token = ?;
            """.trimIndent()
        ).apply {
            setString(1, token)
        }
        .executeUpdate() > 0

    fun deleteSessionByUser(linkUser: String): Int = connection
        .prepareStatement(
            """
            DELETE FROM sessions
            WHERE linkUser = ?;
        """.trimIndent()
        ).apply {
            setString(1, linkUser)
        }
        .executeUpdate()


    fun userdataExist(name: String): Boolean = connection
        .prepareStatement("SELECT 1 FROM userdata WHERE name = ?;")
        .apply {
            setString(1, name)
        }
        .executeQuery()
        .next()

    fun getUserdataIgnores(name: String): MutableSet<String> = connection
        .prepareStatement("SELECT ignores FROM userdata WHERE name = ?;")
        .apply {
            setString(1, name)
        }
        .executeQuery()
        .takeIf { it.next() }
        ?.getString("ignores")
        ?.split(",")
        ?.filter { it.isNotEmpty() }
        ?.toMutableSet()
        ?: mutableSetOf()

    fun setUserdataIgnores(name: String, ignores: Set<String>): Boolean = connection
        .prepareStatement(
            if (userdataExist(name)) {
                "UPDATE userdata SET ignores = ? WHERE name = ?"
            } else {
                "INSERT INTO userdata (ignores, name) VALUES (?, ?)"
            }
        ).apply {
            setString(1, ignores.joinToString(","))
            setString(2, name)
        }.executeUpdate() > 0

}