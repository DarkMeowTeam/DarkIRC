package net.darkmeow.irc.client.network;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.darkmeow.irc.utils.CryptUtils;
import org.jetbrains.annotations.NotNull;

import java.security.PrivateKey;
import java.util.Base64;

@AllArgsConstructor
public class IRCClientVerifyKey {

    @Getter
    @NotNull
    private final PrivateKey key;

    public IRCClientVerifyKey(byte[] data) throws Exception {
        key = CryptUtils.loadPrivateKeyFromByte(data);
    }

    public IRCClientVerifyKey(@NotNull String src) throws Exception {
        this(Base64.getDecoder().decode(src.replace("\n", "").replace(" ", "")));
    }


}
