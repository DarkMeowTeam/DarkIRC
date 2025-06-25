package net.darkmeow.irc.web.route.routes.api

import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import net.darkmeow.irc.IRCServer
import net.darkmeow.irc.data.base.DataClient
import net.darkmeow.irc.database.extensions.DataManagerClientExtensions.createClient
import net.darkmeow.irc.database.extensions.DataManagerClientExtensions.deleteClient
import net.darkmeow.irc.database.extensions.DataManagerClientExtensions.getClientMetadata
import net.darkmeow.irc.database.extensions.DataManagerClientExtensions.getClients
import net.darkmeow.irc.web.route.routes.respondSuccess
import java.util.*

fun Route.routeApiClients(system: IRCServer) {
    route("clients") {
        get("/query") {
            val name = call.parameters["name"] ?: throw Exception("参数 name 不存在或无效")

            call.respondSuccess(ResponseClientQuery(data = system.dataManager.getClientMetadata(name = name)))
        }
        get("/delete") {
            val name = call.parameters["name"] ?: throw Exception("参数 name 不存在或无效")

            system.dataManager.deleteClient(name = name)

            call.respondSuccess(null)
        }
        get("/create") {
            val name = call.parameters["name"] ?: throw Exception("参数 name 不存在或无效")

            call.respondSuccess(ResponseClientQuery(data = system.dataManager.createClient(name = name, metadata = DataClient.ClientMetadata(0))))
        }
        get("/list") {
            call.respondSuccess(ResponseListClients(system.dataManager.getClients().map { ResponseClientQuery(data = it) }))
        }
    }
}

@Serializable
class ResponseClientQuery(val name: String, val allowLoginMinVersion: Int, val key: String) {
    constructor(data: DataClient) : this(
        name = data.name,
        allowLoginMinVersion = data.metadata.allowLoginMinVersion,
        key = Base64.getEncoder().encodeToString(data.key.private.encoded)
    )
}

@Serializable
class ResponseListClients(val total: Int, val clients: Set<ResponseClientQuery>) {
    constructor(clients: Collection<ResponseClientQuery>) : this(clients.size, clients.toSet())
}