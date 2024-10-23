package net.darkmeow.irc

import net.darkmeow.irc.config.ConfigManager
import net.darkmeow.irc.network.NetworkManager

class IRCServer {

    val configManager = ConfigManager()
    val networkManager = NetworkManager(this)

    fun start() {
        configManager.readConfig()
        networkManager.start()
    }

}

