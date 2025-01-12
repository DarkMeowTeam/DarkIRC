package net.darkmeow.irc.web.utils.throwables


class ParamInvalidException (
    val param: String
): WebServerHandleException()