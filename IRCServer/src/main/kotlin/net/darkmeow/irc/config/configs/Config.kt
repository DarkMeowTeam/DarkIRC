package net.darkmeow.irc.config.configs

@Suppress("Unused")
class Config {
    var port: Int = 8080
    var key: String = "publicIRCTest123"

    var database: String = "jdbc:sqlite:data.db"
}
