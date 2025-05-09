package net.darkmeow.irc.database.exceptions

class DataUserAlreadyExistException(val username: String): Exception("用户已存在: $username")