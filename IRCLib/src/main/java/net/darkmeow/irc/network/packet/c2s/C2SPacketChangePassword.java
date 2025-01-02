package net.darkmeow.irc.network.packet.c2s;

import org.jetbrains.annotations.NotNull;

public class C2SPacketChangePassword implements C2SPacket {

    /**
     * 密码
     * 请在组包前进行不可逆加密
     */
    @NotNull
    public String password;

    public C2SPacketChangePassword(@NotNull String password) {
        this.password = password;
    }

}
