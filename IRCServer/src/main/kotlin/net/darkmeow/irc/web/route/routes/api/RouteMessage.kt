package net.darkmeow.irc.web.route.routes.api

import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import net.darkmeow.irc.IRCServer
import net.darkmeow.irc.utils.MessageUtils.sendServiceMessage
import net.darkmeow.irc.web.route.routes.respondSuccess

fun Route.routeApiMessage(system: IRCServer) {
    route("message") {
        get("/service") {
            val message = call.parameters["message"] ?: throw Exception("参数 message 不存在或无效")
            val service = call.parameters["service"] ?: "system"

            var success = 0
            var failed = 0

            system.networkManager.clients.values.forEach { other ->
                runCatching {
                    other.sendServiceMessage(base = system, service = service, message = message)
                }
                    .onSuccess {
                        success++
                    }
                    .onFailure {
                        failed++
                    }
            }

            call.respondSuccess(ResponseSendCount(success = success, failed = failed))
        }
    }
}

@Serializable
class ResponseSendCount(val total: Int, val success: Int, val failed: Int) {
    constructor(success: Int, failed: Int) : this(success + failed, success, failed)
}