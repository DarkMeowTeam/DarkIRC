package net.darkmeow.irc

import net.darkmeow.irc.command.CommandManager
import net.darkmeow.irc.config.ConfigManager
import net.darkmeow.irc.database.DataBaseManager
import net.darkmeow.irc.network.NetworkManager
import net.darkmeow.irc.web.WebServerManager
import org.apache.logging.log4j.LogManager

class IRCServer {

    @JvmField
    val logger = LogManager.getLogger("IRCServer")

    val configManager = ConfigManager()
    val networkManager = NetworkManager(this)
    val dataManager = DataBaseManager(this)
    val commandManager = CommandManager(this)
    val webServerManager = WebServerManager(this)

    fun start() {
        logger.info("DarkIRC")

        configManager.readConfig()
        dataManager.connect()
        networkManager.start()
        webServerManager.start()

        Runtime.getRuntime().addShutdownHook(Thread {
            webServerManager.stop()
            networkManager.stop()
        })
    }

}

