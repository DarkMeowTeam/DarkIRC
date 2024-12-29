package net.darkmeow.irc

import net.darkmeow.irc.client.IRCClient
import net.darkmeow.irc.client.data.IRCOtherUserInfo
import net.darkmeow.irc.client.enums.EnumPremium
import net.darkmeow.irc.client.listener.IRCClientListenableProvide
import net.darkmeow.irc.client.network.IRCClientConnection
import net.darkmeow.irc.data.ClientBrandData
import net.darkmeow.irc.data.GameInfoData
import net.darkmeow.irc.network.packet.c2s.C2SPacketLogin

fun main(args: Array<String>) {
    val client = IRCClient()


    class IRCLegacy: IRCClientListenableProvide {
        override fun onMessageSystem(message: String?) {

        }

        override fun onUpdateUserInfo(name: String?, rank: String?, premium: EnumPremium?) {

        }

        override fun onMessagePublic(sender: IRCOtherUserInfo?, message: String?) {
            println(message)
        }

        override fun onMessagePrivate(sender: IRCOtherUserInfo?, message: String?) {

        }
    }

    client.listenable = IRCLegacy()
    client.connect("45.207.199.139", 48088, "publicIRCTest123")
   // readLine()
    client.login("NekoCurit", "114514", "14451", ClientBrandData("DarkMeow", "114514", 0)) {
        true
    }
    while(true) {
        readLine()?.also {
            client.sendMessage(it)
        }
    }

}