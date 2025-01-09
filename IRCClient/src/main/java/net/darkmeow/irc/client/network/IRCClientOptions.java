package net.darkmeow.irc.client.network;

import net.darkmeow.irc.utils.DeviceUtils;
import org.jetbrains.annotations.NotNull;

import java.net.Proxy;

public class IRCClientOptions {

    @NotNull
    public String host;

    public int port;

    @NotNull
    public String key;

    @NotNull
    public Proxy proxy;

    @NotNull
    public String deviceId;

    public IRCClientOptions(@NotNull String host, int port, @NotNull String key) {
        this.host = host;
        this.port = port;
        this.key = key;
        this.proxy = Proxy.NO_PROXY;
        this.deviceId = DeviceUtils.getDeviceId();
    }

    public IRCClientOptions(@NotNull String host, int port, @NotNull String key, @NotNull Proxy proxy) {
        this.host = host;
        this.port = port;
        this.key = key;
        this.proxy = proxy;
        this.deviceId = DeviceUtils.getDeviceId();
    }

    public IRCClientOptions(@NotNull String host, int port, @NotNull String key, @NotNull String deviceId) {
        this.host = host;
        this.port = port;
        this.key = key;
        this.proxy = Proxy.NO_PROXY;
        this.deviceId = deviceId;
    }

    public IRCClientOptions(@NotNull String host, int port, @NotNull String key, @NotNull Proxy proxy, @NotNull String deviceId) {
        this.host = host;
        this.port = port;
        this.key = key;
        this.proxy = proxy;
        this.deviceId = deviceId;
    }

}
