package net.darkmeow.irc.web.api.api

import net.darkmeow.irc.utils.ChannelUtils.sendServiceMessage
import net.darkmeow.irc.web.APIHandler
import net.darkmeow.irc.web.Handle
import net.darkmeow.irc.web.Response
import net.darkmeow.irc.web.utils.throwables.ParamNotFoundException

class BoardCastServiceMessage : APIHandler {
    override fun handle(handle: Handle): Response {
        val response = Response()

        val service = handle.requestParams["service"] ?: run { throw ParamNotFoundException("service") }
        val message = handle.requestParams["message"] ?: run { throw ParamNotFoundException("message") }

        handle.manager.base.networkManager.clients.onEach { (_, channel) ->
            channel.sendServiceMessage(handle.manager.base, service, message)
        }

        response.code = 200
        response.msg = "发送成功"
        response.data.addProperty("count", handle.manager.base.networkManager.clients.size)

        return response
    }
}
