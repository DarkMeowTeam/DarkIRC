package net.darkmeow.irc.client.options.proxy;

import io.netty.channel.ChannelHandler;
import org.jetbrains.annotations.Nullable;

public class IRCOptionsProxyNone implements IRCOptionsProxy {

    @Override
    public @Nullable ChannelHandler getNettyHandler() {
        return null;
    }

}
