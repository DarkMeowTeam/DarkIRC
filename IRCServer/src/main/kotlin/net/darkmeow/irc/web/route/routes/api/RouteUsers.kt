package net.darkmeow.irc.web.route.routes.api

import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import net.darkmeow.irc.IRCServer
import net.darkmeow.irc.data.DataClientBrand
import net.darkmeow.irc.data.base.DataUser
import net.darkmeow.irc.data.enmus.EnumUserPremium
import net.darkmeow.irc.database.extensions.DataManagerUserExtensions.createUser
import net.darkmeow.irc.database.extensions.DataManagerUserExtensions.deleteUser
import net.darkmeow.irc.database.extensions.DataManagerUserExtensions.getUserMetadata
import net.darkmeow.irc.database.extensions.DataManagerUserExtensions.getUsers
import net.darkmeow.irc.database.extensions.DataManagerUserExtensions.updateUserMetadata
import net.darkmeow.irc.network.IRCNetworkManagerServer
import net.darkmeow.irc.network.packet.online.s2c.S2CPacketUpdateMyProfile
import net.darkmeow.irc.web.route.routes.respondSuccess

fun Route.routeApiUsers(system: IRCServer) {
    route("users") {
        get("/query") {
            val name = call.parameters["name"] ?: throw Exception("参数 name 不存在或无效")

            call.respondSuccess(ResponseUserQuery(system = system, data = system.dataManager.getUserMetadata(name = name)))
        }
        get("/update/premium") {
            val name = call.parameters["name"] ?: throw Exception("参数 name 不存在或无效")
            val premium = call.parameters["premium"]?.toIntOrNull() ?: throw Exception("参数 premium 不存在或无效")
            if (premium !in 0..EnumUserPremium.entries.size) throw Exception("无效的等级范围")

            val newPremium = EnumUserPremium.entries[premium]

            system.dataManager.updateUserMetadata(name = name, premium = newPremium)
            system.networkManager.clients.values
                .filter { other -> other.user == name }
                .onEach { other ->
                    other.userPremium = newPremium
                    other.sendPacket(S2CPacketUpdateMyProfile(other.user, newPremium, other.currentIsInvisible))
                }

            call.respondSuccess(null)
        }
        get("/update/password") {
            val name = call.parameters["name"] ?: throw Exception("参数 name 不存在或无效")
            val password = call.parameters["password"] ?: throw Exception("参数 password 不存在或无效")

            system.dataManager.updateUserMetadata(name = name, password = password)
            call.respondSuccess(null)
        }
        get("/delete") {
            val name = call.parameters["name"] ?: throw Exception("参数 name 不存在或无效")

            system.dataManager.deleteUser(name = name)
            system.networkManager.clients.values
                .filter { other -> other.user == name }
                .onEach { other -> other.kick(reason = "账号被管理员删除", logout = true) }

            call.respondSuccess(null)
        }
        get("/create") {
            val name = call.parameters["name"] ?: throw Exception("参数 name 不存在或无效")
            val password = call.parameters["password"] ?: throw Exception("参数 password 不存在或无效")
            val premium = call.parameters["premium"]?.toIntOrNull() ?: throw Exception("参数 premium 不存在或无效")
            if (premium !in 0..EnumUserPremium.entries.size) throw Exception("无效的等级范围")

            system.dataManager.createUser(name = name, password = password, premium = EnumUserPremium.entries[premium])
            call.respondSuccess(null)
        }
        get("/list") {
            call.respondSuccess(ResponseListUsers(system.dataManager.getUsers().map { ResponseUserQuery(system = system, data = it) }))
        }
    }
}

@Serializable
class ResponseUserQuery(val name: String, val premium: Int, val online: Set<ResponseUserQueryOnlineClient>) {
    constructor(system: IRCServer, data: DataUser) : this(
        name = data.name,
        premium = data.metadata.premium.ordinal,
        online = system.networkManager.clients.values
            .filter { it.user == data.name }
            .map { ResponseUserQueryOnlineClient(it) }
            .toMutableSet()
    )
}

@Serializable
class ResponseUserQueryOnlineClient(val session: String, val hardware: String, val ip: String, val brand: ResponseUserQueryOnlineClientBrand) {
    constructor(client: IRCNetworkManagerServer) : this(
        session = client.sessionId.toString(),
        hardware = client.hardWareUniqueId,
        ip = client.address,
        brand = ResponseUserQueryOnlineClientBrand(client.brand)
    )
}

@Serializable
class ResponseUserQueryOnlineClientBrand(val name: String, val version: String) {
    constructor(brand: DataClientBrand) : this(
        name = brand.name,
        version = brand.versionText
    )
}

@Serializable
class ResponseListUsers(val total: Int, val users: Set<ResponseUserQuery>) {
    constructor(users: Collection<ResponseUserQuery>) : this(users.size, users.toMutableSet())
}