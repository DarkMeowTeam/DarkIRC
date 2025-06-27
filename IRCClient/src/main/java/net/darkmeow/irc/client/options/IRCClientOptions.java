package net.darkmeow.irc.client.options;

import lombok.AllArgsConstructor;
import lombok.Builder;
import net.darkmeow.irc.client.options.proxy.IRCOptionsProxy;
import net.darkmeow.irc.data.DataClientBrand;
import net.darkmeow.irc.utils.FakeHardwareUniqueIdGetter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.Proxy;

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
    @Nullable
    @Builder.Default
    public IRCOptionsProxy proxy = null;

    /**
     * 上报的硬件唯一ID信息
     */
    @NotNull
    @Builder.Default
    public String deviceId = FakeHardwareUniqueIdGetter.get();

    /**
     * 如果服务端启用签名验证
     * 这里需要提供用于验证自身身份的密钥
     */
    @Nullable
    @Builder.Default
    public IRCClientSignatureKey clientKey = null;

    /**
     * 验证远程服务端身份
     * 留空则不验证
     */
    @Nullable
    @Builder.Default
    public IRCClientRemoteVerify remoteVerify = null;

    /**
     * 客户端·标识
     * 必填
     */
    @NotNull
    public DataClientBrand brand;

}
