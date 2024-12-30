package net.darkmeow.irc

import net.darkmeow.irc.command.CommandManager
import net.darkmeow.irc.config.ConfigManager
import net.darkmeow.irc.data.DataManager
import net.darkmeow.irc.network.NetworkManager
import org.apache.logging.log4j.LogManager

class IRCServer {

    @JvmField
    val logger = LogManager.getLogger("IRCServer")

    val configManager = ConfigManager()
    val networkManager = NetworkManager(this)
    val dataManager = DataManager(this)
    val commandManager = CommandManager()

    fun start() {
        configManager.readConfig()
        dataManager.connect(configManager.configs.database)
        networkManager.start(configManager.configs.port)
    }

}

