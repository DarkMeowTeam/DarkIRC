package net.darkmeow.irc.network

import io.netty.util.AttributeKey

object AttributeKeys {
    val LATEST_KEEPALIVE: AttributeKey<Long> = AttributeKey.newInstance("latestKeepAlive")

}