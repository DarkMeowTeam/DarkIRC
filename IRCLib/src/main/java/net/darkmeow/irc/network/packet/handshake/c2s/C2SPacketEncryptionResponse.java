package net.darkmeow.irc.network.packet.handshake.c2s;

import net.darkmeow.irc.network.FriendBuffer;
import net.darkmeow.irc.network.packet.C2SPacket;
import net.darkmeow.irc.utils.CryptUtils;
import org.jetbrains.annotations.NotNull;

import javax.crypto.SecretKey;
import java.security.PrivateKey;
import java.security.PublicKey;

public class C2SPacketEncryptionResponse implements C2SPacket {

    private final byte[] secretKeyEncrypted;

    public C2SPacketEncryptionResponse(PublicKey publicKey, SecretKey secretKey) {
        this.secretKeyEncrypted = CryptUtils.encryptData(publicKey, secretKey.getEncoded());
    }

    public C2SPacketEncryptionResponse(@NotNull FriendBuffer buffer) {
        this.secretKeyEncrypted = buffer.readByteArray();
    }

    @Override
    public void write(@NotNull FriendBuffer buffer) {
        buffer.writeByteArray(this.secretKeyEncrypted);
    }

    public @NotNull SecretKey getSecretKey(@NotNull PrivateKey key)
    {
        return CryptUtils.decryptSharedKey(key, this.secretKeyEncrypted);
    }
}
