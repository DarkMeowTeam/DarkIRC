package net.darkmeow.irc.web.route

import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.basic
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import net.darkmeow.irc.IRCServer
import net.darkmeow.irc.web.route.routes.RouteResponseBase
import org.slf4j.event.Level

fun Application.installBase(system: IRCServer) {
    install(ContentNegotiation) {
        json()
    }

    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respond(RouteResponseBase(400, cause.message ?: "未知错误", null))
        }
        status(HttpStatusCode.NotFound) { call, _ ->
            call.respond(RouteResponseBase(404, "404 Not Found", null))
        }
    }

    if (system.configManager.configs.webServer.key.isNotEmpty()) {
        install(Authentication) {
            basic("api") {
                realm = "Access to the site"

                validate { credentials ->
                    println(credentials.name + "  " + credentials.password)
                    if (credentials.password == system.configManager.configs.webServer.key) {
                        UserIdPrincipal(credentials.name)
                    } else null
                }
            }
        }
    }


    install(CallLogging) {
        level = Level.INFO
        filter { call ->
            call.request.path().startsWith("/api")
        }
        format { call ->
            "${ call.request.httpMethod.value} ${call.request.uri} ${call.request.getRemoteAddress()}"
        }
    }
}

fun ApplicationRequest.getRemoteAddress() = "${call.request.local.remoteHost}:${call.request.local.remotePort}"