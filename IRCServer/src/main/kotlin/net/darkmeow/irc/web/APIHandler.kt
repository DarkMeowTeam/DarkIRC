package net.darkmeow.irc.web

interface APIHandler {
    fun handle(handle: Handle): Response
}