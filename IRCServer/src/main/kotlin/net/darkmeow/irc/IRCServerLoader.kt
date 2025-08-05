package net.darkmeow.irc

object IRCServerLoader {

    @JvmStatic
    fun main(vararg args: String) {
        IRCServer().start()
    }

}