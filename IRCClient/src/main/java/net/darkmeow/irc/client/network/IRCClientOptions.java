package net.darkmeow.irc.client.network;

import net.darkmeow.irc.data.DataClientBrand;
import net.darkmeow.irc.utils.DeviceUtils;
import org.jetbrains.annotations.NotNull;

import java.net.Proxy;

public class IRCClientOptions {

    @NotNull
    public String host;

    public int port;

    @NotNull
    public Proxy proxy;

    @NotNull
    public String deviceId;

    @NotNull
    public DataClientBrand brand;

    public IRCClientOptions(@NotNull String host, int port, @NotNull DataClientBrand brand) {
        this.host = host;
        this.port = port;
        this.brand = brand;
        this.proxy = Proxy.NO_PROXY;
        this.deviceId = DeviceUtils.getDeviceId();
    }

    public IRCClientOptions(@NotNull String host, int port, @NotNull DataClientBrand brand, @NotNull Proxy proxy) {
        this.host = host;
        this.port = port;
        this.brand = brand;
        this.proxy = proxy;
        this.deviceId = DeviceUtils.getDeviceId();
    }

    public IRCClientOptions(@NotNull String host, int port, @NotNull DataClientBrand brand, @NotNull String deviceId) {
        this.host = host;
        this.port = port;
        this.brand = brand;
        this.proxy = Proxy.NO_PROXY;
        this.deviceId = deviceId;
    }

    public IRCClientOptions(@NotNull String host, int port, @NotNull DataClientBrand brand, @NotNull Proxy proxy, @NotNull String deviceId) {
        this.host = host;
        this.port = port;
        this.brand = brand;
        this.proxy = proxy;
        this.deviceId = deviceId;
    }

}
