package net.darkmeow.irc.network.packet.c2s;

import net.darkmeow.irc.data.ClientBrandData;
import org.jetbrains.annotations.NotNull;

public class C2SPacketLogin implements C2SPacket {

    /**
     * 用户名
     */
    @NotNull
    public String name;

    /**
     * 密码
     * 请在组包前进行不可逆加密
     */
    @NotNull
    public String password;

    /**
     * 客户端标识
     */
    @NotNull
    public ClientBrandData client;

    /**
     * 只验证用户名密码不上线
     */
    public boolean notOnline;

    public C2SPacketLogin(@NotNull String name, @NotNull String password, @NotNull ClientBrandData client) {
        this.name = name;
        this.password = password;
        this.client = client;
        this.notOnline = false;
    }

    public C2SPacketLogin(@NotNull String name, @NotNull String password, @NotNull ClientBrandData client, boolean notOnline) {
        this.name = name;
        this.password = password;
        this.client = client;
        this.notOnline = notOnline;
    }

}
