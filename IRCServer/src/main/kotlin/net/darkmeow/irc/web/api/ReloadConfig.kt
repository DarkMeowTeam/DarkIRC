package net.darkmeow.irc.web.api

import com.google.gson.Gson
import net.darkmeow.irc.web.APIHandler
import net.darkmeow.irc.web.Handle
import net.darkmeow.irc.web.Response

class ReloadConfig: APIHandler {
    override fun handle(handle: Handle): Response {
        val response = Response()

        handle.manager.base.configManager.readConfig()

        response.code = 200
        response.msg = "重新加载配置成功"

        response.data.add("config", Gson().toJsonTree(handle.manager.base.configManager.configs))

        return response
    }
}
