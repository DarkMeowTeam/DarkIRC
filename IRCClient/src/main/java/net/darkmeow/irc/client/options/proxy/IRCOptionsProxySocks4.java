package net.darkmeow.irc.client.options.proxy;

import io.netty.channel.ChannelHandler;
import io.netty.handler.proxy.Socks4ProxyHandler;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.InetSocketAddress;

@AllArgsConstructor
public class IRCOptionsProxySocks4 implements IRCOptionsProxy {

    @NotNull
    public String host;

    public int port;

    @Nullable
    public String username;

    @Override
    public @Nullable ChannelHandler getNettyHandler() {
        return new Socks4ProxyHandler(new InetSocketAddress(host, port), username);
    }

}
