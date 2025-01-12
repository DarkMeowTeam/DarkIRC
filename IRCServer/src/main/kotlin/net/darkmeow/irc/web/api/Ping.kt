package net.darkmeow.irc.web.api

import net.darkmeow.irc.web.APIHandler
import net.darkmeow.irc.web.Handle
import net.darkmeow.irc.web.Response
import java.time.Instant

class Ping : APIHandler {
    override fun handle(handle: Handle): Response {
        val response = Response()

        response.code = 200
        response.msg = "Pong!"
        response.data.addProperty("now", Instant.now().toEpochMilli() / 1000)

        return response
    }
}
