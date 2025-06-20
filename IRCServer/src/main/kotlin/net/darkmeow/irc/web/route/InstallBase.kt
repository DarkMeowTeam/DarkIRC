package net.darkmeow.irc.web.route

import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.AuthenticationContext
import io.ktor.server.auth.AuthenticationFailedCause
import io.ktor.server.auth.AuthenticationProvider
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.authentication
import io.ktor.server.auth.basic
import io.ktor.server.auth.bearer
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.origin
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

    install(Authentication) {
        register(object : AuthenticationProvider(Config("api")) {
            override suspend fun onAuthenticate(context: AuthenticationContext) {
                val key = context.call.request.header("Authorization")?.removePrefix("Bearer ")
                val ip = context.call.request.origin.remoteHost

                runCatching {
                    system.configManager.configs.webServer.key
                        .takeIf { it.isNotEmpty() }
                        ?.also { keyConfig ->
                            if (key != keyConfig) throw Exception()
                        }
                    system.configManager.configs.webServer.ipWhiteList
                        .takeIf { it.isNotEmpty() }
                        ?.also { ipWhiteList ->
                            if (!ipWhiteList.contains(ip)) throw Exception()
                        }
                }
                    .onSuccess {
                        context.principal(UserIdPrincipal("api"))
                    }
                    .onFailure {
                        context.challenge("api", AuthenticationFailedCause.InvalidCredentials) { challenge, call ->
                            call.respond(RouteResponseBase(403, "无访问权限", null))
                            challenge.complete()
                        }
                    }
            }
        })
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

class Config(name: String?) : AuthenticationProvider.Config(name)
