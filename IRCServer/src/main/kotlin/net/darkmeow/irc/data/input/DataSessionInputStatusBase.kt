package net.darkmeow.irc.data.input

import net.darkmeow.irc.data.enmus.EnumChatType

abstract class DataSessionInputStatusBase(
    val timestamp: Long = System.currentTimeMillis(),
    val type: EnumChatType
)