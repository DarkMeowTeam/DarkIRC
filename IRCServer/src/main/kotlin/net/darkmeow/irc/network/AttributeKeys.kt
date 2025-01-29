package net.darkmeow.irc.network

import io.netty.util.AttributeKey
import net.darkmeow.irc.data.DataSessionInfo
import net.darkmeow.irc.data.DataSessionOptions
import java.util.*

object AttributeKeys {
    val PROTOCOL: AttributeKey<Int> = AttributeKey.newInstance("protocol")
    val UUID: AttributeKey<UUID> = AttributeKey.newInstance("uuid")
    val ADDRESS: AttributeKey<String> = AttributeKey.newInstance("address")
    val DEVICE: AttributeKey<String> = AttributeKey.newInstance("device")
    val LATEST_KEEPALIVE: AttributeKey<Long> = AttributeKey.newInstance("latestKeepAlive")

    val CURRENT_USER: AttributeKey<String> = AttributeKey.newInstance("currentUser")
    val CURRENT_TOKEN: AttributeKey<String> = AttributeKey.newInstance("currentToken")

    val SESSION_INFO: AttributeKey<DataSessionInfo> = AttributeKey.newInstance("sessionInfo")
    val SESSION_OPTIONS: AttributeKey<DataSessionOptions> = AttributeKey.newInstance("sessionOptions")
    val SESSION_IS_INVISIBLE: AttributeKey<Boolean> = AttributeKey.newInstance("sessionIsInvisible")

}