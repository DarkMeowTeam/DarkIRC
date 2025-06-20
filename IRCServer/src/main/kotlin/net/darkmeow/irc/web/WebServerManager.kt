package net.darkmeow.irc.web

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import net.darkmeow.irc.IRCServer
import net.darkmeow.irc.web.route.installBase
import net.darkmeow.irc.web.route.routes.routeApi
import net.darkmeow.irc.web.route.routes.routeBase

class WebServerManager(
    val system: IRCServer
) {
    fun start() {
        embeddedServer(factory = Netty, host = system.configManager.configs.webServer.host, port = system.configManager.configs.webServer.port) {
            installBase(system)

            routing {
                routeBase()
                routeApi(system)
            }
        }.start(wait = false)
    }
}
