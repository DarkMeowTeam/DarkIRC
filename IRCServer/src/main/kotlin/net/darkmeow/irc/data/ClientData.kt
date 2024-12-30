package net.darkmeow.irc.data

import net.darkmeow.irc.data.datas.ConnectData
import net.darkmeow.irc.data.datas.SessionData
import java.util.*

data class ClientData(
    val uuid: UUID,
    val connectData: ConnectData,
    var session: SessionData? = null
)