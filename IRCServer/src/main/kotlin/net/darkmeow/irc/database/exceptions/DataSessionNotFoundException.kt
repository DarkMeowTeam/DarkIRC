package net.darkmeow.irc.database.exceptions

class DataSessionNotFoundException(val token: String): Exception("会话凭据不存在: $token")