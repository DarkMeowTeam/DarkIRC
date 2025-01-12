package net.darkmeow.irc.web.utils.throwables


class ParamNotFoundException (
    val param: String
): WebServerHandleException()