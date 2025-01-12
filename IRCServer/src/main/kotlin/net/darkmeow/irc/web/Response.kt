package net.darkmeow.irc.web

import com.google.gson.JsonObject

class Response {
    var code: Int = 200
    var msg: String = ""
    var data: JsonObject = JsonObject()
    var headerContentType: String = "application/json; charset=UTF-8"
}
