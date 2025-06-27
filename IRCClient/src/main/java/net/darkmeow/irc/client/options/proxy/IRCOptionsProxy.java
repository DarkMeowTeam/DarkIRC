package net.darkmeow.irc.client.options.proxy;

import io.netty.channel.ChannelHandler;
import org.jetbrains.annotations.Nullable;

public interface IRCOptionsProxy {
    @Nullable
    ChannelHandler getNettyHandler();
}
