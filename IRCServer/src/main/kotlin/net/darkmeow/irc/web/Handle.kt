package net.darkmeow.irc.web

class Handle (
    val manager: WebServerManager,
    var requestPath: String,
    var requestParams: Map<String, String> = hashMapOf()
)
