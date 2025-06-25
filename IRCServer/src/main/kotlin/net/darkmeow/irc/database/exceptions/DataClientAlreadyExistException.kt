package net.darkmeow.irc.database.exceptions

class DataClientAlreadyExistException(val name: String): Exception("客户端已存在: $name")