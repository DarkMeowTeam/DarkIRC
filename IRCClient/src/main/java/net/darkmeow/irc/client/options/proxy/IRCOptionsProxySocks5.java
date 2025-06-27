package net.darkmeow.irc.client.options.proxy;

import io.netty.channel.ChannelHandler;
import io.netty.handler.proxy.HttpProxyHandler;
import io.netty.handler.proxy.Socks5ProxyHandler;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.InetSocketAddress;

@AllArgsConstructor
public class IRCOptionsProxySocks5 implements IRCOptionsProxy {

    @NotNull
    public String host;

    public int port;

    @Nullable
    public String username;

    @Nullable
    public String password;

    @Override
    public @Nullable ChannelHandler getNettyHandler() {
        return new Socks5ProxyHandler(new InetSocketAddress(host, port), username, password);
    }

}
