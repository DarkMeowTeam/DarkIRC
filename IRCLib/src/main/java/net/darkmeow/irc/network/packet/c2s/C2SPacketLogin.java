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
     * 登录模式
     */
    @NotNull
    public Mode mode;

    public enum Mode {
        /**
         * 正常登录
         */
        NORMAL,
        /**
         * 隐身登录 （其他人无法在 IRC 中看到 友好模式将被关闭)
         */
        INVISIBLE,
        /**
         * 仅验证用户名密码是否正确 (不上线)
         */
        ONLY_VERIFY_PASSWORD
    }

    public C2SPacketLogin(@NotNull String name, @NotNull String password, @NotNull ClientBrandData client) {
        this.name = name;
        this.password = password;
        this.client = client;
        this.mode = Mode.NORMAL;
    }

    public C2SPacketLogin(@NotNull String name, @NotNull String password, @NotNull ClientBrandData client, @NotNull Mode mode) {
        this.name = name;
        this.password = password;
        this.client = client;
        this.mode = mode;
    }

}
