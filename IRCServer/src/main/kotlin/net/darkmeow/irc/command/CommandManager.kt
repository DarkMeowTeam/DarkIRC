package net.darkmeow.irc.command

import io.netty.channel.Channel
import net.darkmeow.irc.IRCServer
import net.darkmeow.irc.command.impl.CommandClients
import net.darkmeow.irc.command.impl.CommandPing
import net.darkmeow.irc.command.impl.CommandUsers

class CommandManager(
    val base: IRCServer
) {

    @JvmField
    val commands = arrayOf(
        CommandPing(),
        CommandUsers(),
        CommandClients()
    )

    fun handle(ctx: Channel, root: String, args: MutableList<String>) = commands
        .firstOrNull { it.root.any { r -> r.equals(other = root, ignoreCase = true) }}
        ?.runCatching { handle(this@CommandManager, ctx, args) }
        ?.onFailure { t ->
            base.logger.error("处理命令(root=$root, args=${args.joinToString(" ")}})时发生异常", t)
        }

}