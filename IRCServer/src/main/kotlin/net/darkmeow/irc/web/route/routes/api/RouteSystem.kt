package net.darkmeow.irc.web.route.routes.api

import io.ktor.server.routing.*
import net.darkmeow.irc.IRCServer
import net.darkmeow.irc.web.route.routes.respondSuccess

fun Route.routeApiSystem(system: IRCServer) {
    route("system") {
        get("/reload") {
            system.configManager.readConfig()

            call.respondSuccess(null)
        }
        get("/log") {
            val message = call.parameters["message"] ?: throw Exception("参数 message 不存在或无效")

            system.logger.info(message)
            call.respondSuccess(null)
        }
    }
}