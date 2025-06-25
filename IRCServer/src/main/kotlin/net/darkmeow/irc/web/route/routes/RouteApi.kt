package net.darkmeow.irc.web.route.routes

import io.ktor.server.auth.authenticate
import io.ktor.server.routing.*
import net.darkmeow.irc.IRCServer
import net.darkmeow.irc.web.route.routes.api.routeApiClients
import net.darkmeow.irc.web.route.routes.api.routeApiMessage
import net.darkmeow.irc.web.route.routes.api.routeApiSystem
import net.darkmeow.irc.web.route.routes.api.routeApiUsers

fun Route.routeApi(system: IRCServer) {
    authenticate("api") {
        route("api") {
            route("v1") {
                routeApiUsers(system)
                routeApiClients(system)
                routeApiMessage(system)
                routeApiSystem(system)
            }
        }
    }
}