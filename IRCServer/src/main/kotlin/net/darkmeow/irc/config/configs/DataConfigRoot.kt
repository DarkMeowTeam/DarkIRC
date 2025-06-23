package net.darkmeow.irc.config.configs

import kotlinx.serialization.Serializable

@Serializable
class DataConfigRoot(
    val database: DataConfigDataBase = DataConfigDataBase(),
    val userLimit: DataConfigUserLimit = DataConfigUserLimit(),
    var server: DataConfigServer = DataConfigServer(),
    var api: DataConfigApi = DataConfigApi()
)