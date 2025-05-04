package net.darkmeow.irc.network.packet.handshake.c2s;

import lombok.Getter;
import net.darkmeow.irc.network.FriendBuffer;
import net.darkmeow.irc.network.packet.C2SPacket;
import net.darkmeow.irc.utils.CryptUtils;
import org.jetbrains.annotations.NotNull;

import java.security.PrivateKey;
import java.security.PublicKey;

public class C2SPacketSignatureResponse implements C2SPacket {

    @Getter
    @NotNull
    private final String code;

    @Getter
    private final byte[] signature;

    public C2SPacketSignatureResponse(@NotNull String code, PrivateKey key) throws Exception {
        this.code = code;
        this.signature = CryptUtils.signCode(code, key);
    }

    public C2SPacketSignatureResponse(@NotNull FriendBuffer buffer) {
        this.code = buffer.readString(32767);
        this.signature = buffer.readByteArray();
    }

    @Override
    public void write(@NotNull FriendBuffer buffer) {
        buffer.writeString(code);
        buffer.writeByteArray(this.signature);
    }

    public boolean verify(@NotNull PublicKey key) throws Exception {
        return CryptUtils.verifyCode(code, signature, key);
    }
}
