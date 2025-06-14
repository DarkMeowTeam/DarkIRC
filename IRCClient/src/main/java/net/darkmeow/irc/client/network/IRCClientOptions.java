package net.darkmeow.irc.client.network;

import lombok.AllArgsConstructor;
import lombok.Builder;
import net.darkmeow.irc.data.DataClientBrand;
import net.darkmeow.irc.utils.FakeHardwareUniqueIdGetter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.Proxy;
import java.security.PrivateKey;

@Builder
@AllArgsConstructor
public class IRCClientOptions {

    /**
     * 连接域名 必填
     */
    @NotNull
    public String host;

    /**
     * 连接端口 必填
     */
    public int port;

    /**
     * 连接所使用的代理 选填
     */
    @NotNull
    @Builder.Default
    public Proxy proxy = Proxy.NO_PROXY;

    /**
     * 上报的硬件唯一ID信息
     */
    @NotNull
    @Builder.Default
    public String deviceId = FakeHardwareUniqueIdGetter.get();

    /**
     * 如果服务端启用签名验证
     * 这里需要传入私钥
     */
    @Nullable
    @Builder.Default
    public PrivateKey key = null;

    /**
     * 客户端·标识
     * 必填
     */
    @NotNull
    public DataClientBrand brand;

}
