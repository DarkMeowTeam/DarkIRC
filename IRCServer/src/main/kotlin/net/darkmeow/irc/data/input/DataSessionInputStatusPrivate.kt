package net.darkmeow.irc.data.input

import net.darkmeow.irc.data.enmus.EnumChatType
import java.util.Objects

class DataSessionInputStatusPrivate(val receiver: String): DataSessionInputStatusBase(type = EnumChatType.PRIVATE) {
    override fun hashCode() = Objects.hash(receiver)
    override fun equals(other: Any?) = other?.hashCode() == this.hashCode()
}