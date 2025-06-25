package net.darkmeow.irc.web.route.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

@Suppress("SpellCheckingInspection")
fun Route.routeBase() {
    get("/") {
        call.respondText("DarkIRC is running..")
    }
    get("/robots.txt") {
        call.respondText("""
            User-agent: *
            Disallow: /    
        """.trimIndent())
    }
    get("/sitemap.xml") {
        call.respondText("""
            <?xml version="1.0" encoding="UTF-8"?>
            <urlset xmlns="https://www.sitemaps.org/schemas/sitemap/0.9"/>
        """.trimIndent())
    }
    get("/generate_204") {
        call.respond(HttpStatusCode.NoContent)
    }
}


@Serializable
class RouteResponseBase<T>(val code: Int, val message: String, val data: T)

suspend inline fun <reified T> RoutingCall.respondSuccess(data: T) = respond(RouteResponseBase(0, "", data))