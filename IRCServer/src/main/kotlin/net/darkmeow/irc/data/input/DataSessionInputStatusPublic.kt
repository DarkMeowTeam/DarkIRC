package net.darkmeow.irc.data.input

import net.darkmeow.irc.data.enmus.EnumChatType

class DataSessionInputStatusPublic: DataSessionInputStatusBase(type = EnumChatType.PUBLIC) {
    override fun hashCode() = 0
    override fun equals(other: Any?) = other is DataSessionInputStatusPublic
}