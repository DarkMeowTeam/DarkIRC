package net.darkmeow.irc.web.route.routes

import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

fun Route.routeBase() {
    get("/") {
        call.respondText("DarkIRC is running..")
    }
    get("/robots.txt") {
        call.respondText("User-agent: *\nDisallow: /")
    }
}


@Serializable
class RouteResponseBase<T>(val code: Int, val message: String, val data: T)

suspend inline fun <reified T> RoutingCall.respondSuccess(data: T) = respond(RouteResponseBase(0, "", data))