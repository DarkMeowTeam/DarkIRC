package net.darkmeow.irc.client.network;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.darkmeow.irc.utils.CryptUtils;
import org.jetbrains.annotations.NotNull;

import java.security.PublicKey;
import java.util.Base64;

public class IRCClientRemoteVerify {

    /**
     * 是否已经验证过服务端身份
     */
    public boolean verify = false;

    @Getter
    @NotNull
    private final PublicKey key;


    public IRCClientRemoteVerify(@NotNull PublicKey key) throws Exception {
        this.key = key;
    }

    public IRCClientRemoteVerify(byte[] data) throws Exception {
        this.key = CryptUtils.loadPublicKeyFromByte(data);
    }

    public IRCClientRemoteVerify(@NotNull String src) throws Exception {
        this(Base64.getDecoder().decode(src.replace("\n", "").replace(" ", "")));
    }


}
