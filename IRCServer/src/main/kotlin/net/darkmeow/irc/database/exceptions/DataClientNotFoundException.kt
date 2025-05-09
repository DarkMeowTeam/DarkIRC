package net.darkmeow.irc.database.exceptions

class DataClientNotFoundException(val name: String): Exception("客户端不存在: $name")