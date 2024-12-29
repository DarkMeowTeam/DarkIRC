package net.darkmeow.irc.network

import io.netty.util.AttributeKey
import net.darkmeow.irc.data.GameInfoData
import java.util.UUID

object AttributeKeys {
    val UUID = AttributeKey.newInstance<UUID>("uuid")
    val LATEST_KEEPALIVE = AttributeKey.newInstance<Long>("latestKeepAlive")
    val CURRENT_USER = AttributeKey.newInstance<String>("currentUser")
    val GAME_INFO = AttributeKey.newInstance<GameInfoData>("gameInfo")
}