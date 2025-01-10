package net.darkmeow.irc.network

import io.netty.util.AttributeKey
import net.darkmeow.irc.data.DataSessionInfo
import net.darkmeow.irc.data.DataSessionOptions
import java.util.*

object AttributeKeys {
    val UUID = AttributeKey.newInstance<UUID>("uuid")
    val ADDRESS = AttributeKey.newInstance<String>("address")
    val DEVICE = AttributeKey.newInstance<String>("device")
    val LATEST_KEEPALIVE = AttributeKey.newInstance<Long>("latestKeepAlive")
    val CURRENT_USER = AttributeKey.newInstance<String>("currentUser")

    val SESSION_INFO = AttributeKey.newInstance<DataSessionInfo>("sessionInfo")
    val SESSION_OPTIONS = AttributeKey.newInstance<DataSessionOptions>("sessionOptions")
}