package net.darkmeow.irc.utils

object Log4jProtectUtils {
    val REGEX = Regex("""\$\{jndi:ldap://(.*)}""")

    fun String.isJndiLdap(): Boolean = REGEX.matches(this)
}