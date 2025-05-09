package net.darkmeow.irc.database.exceptions

class DataUserNotFoundException(val username: String): Exception("用户不存在: $username")