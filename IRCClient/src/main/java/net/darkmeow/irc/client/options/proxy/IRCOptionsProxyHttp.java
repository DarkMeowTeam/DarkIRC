package net.darkmeow.irc.client.options.proxy;

import io.netty.channel.ChannelHandler;
import io.netty.handler.proxy.HttpProxyHandler;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.InetSocketAddress;

@AllArgsConstructor
public class IRCOptionsProxyHttp implements IRCOptionsProxy {

    @NotNull
    public String host;

    public int port;

    @Override
    public @Nullable ChannelHandler getNettyHandler() {
        return new HttpProxyHandler(new InetSocketAddress(host, port));
    }

}
