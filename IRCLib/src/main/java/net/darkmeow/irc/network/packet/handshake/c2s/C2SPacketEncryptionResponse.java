package net.darkmeow.irc.network.packet.handshake.c2s;

import lombok.Getter;
import net.darkmeow.irc.network.FriendBuffer;
import net.darkmeow.irc.network.packet.C2SPacket;
import net.darkmeow.irc.utils.ByteUtils;
import net.darkmeow.irc.utils.CryptUtils;
import org.jetbrains.annotations.NotNull;

import javax.crypto.SecretKey;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

public class C2SPacketEncryptionResponse implements C2SPacket {

    @Getter
    private final byte[] secretKeyEncrypted;

    @Getter
    private final byte[] signatureByte;


    /**
     * 加密回应包 (S2CPacketEncryptionRequest.hasSignatureRequire() 为 false)
     *
     * @param publicKey 加密公钥
     * @param secretKey 加密私钥
     */
    public C2SPacketEncryptionResponse(PublicKey publicKey, SecretKey secretKey) {
        this.secretKeyEncrypted = CryptUtils.encryptData(publicKey, secretKey.getEncoded());
        this.signatureByte = new byte[]{};
    }

    /**
     * 加密回应包 (S2CPacketEncryptionRequest.hasSignatureRequire() 为 true)
     *
     * @param publicKey 加密公钥
     * @param secretKey 加密私钥
     * @param signaturePrivateKey 签名密钥
     * @param signatureData 签名数据
     */
    public C2SPacketEncryptionResponse(PublicKey publicKey, SecretKey secretKey, PrivateKey signaturePrivateKey, byte[] signatureData) throws Exception {
        this.secretKeyEncrypted = CryptUtils.encryptData(publicKey, secretKey.getEncoded());
        this.signatureByte = CryptUtils.signData(ByteUtils.concatByteArrays(publicKey.getEncoded(), signatureData), signaturePrivateKey);
    }

    public C2SPacketEncryptionResponse(@NotNull FriendBuffer buffer) {
        this.secretKeyEncrypted = buffer.readByteArray();
        this.signatureByte = buffer.readByteArray();
    }

    @Override
    public void write(@NotNull FriendBuffer buffer) {
        buffer.writeByteArray(this.secretKeyEncrypted);
        buffer.writeByteArray(this.signatureByte);
    }

    public @NotNull SecretKey getSecretKey(@NotNull PrivateKey key) {
        return CryptUtils.decryptSharedKey(key, this.secretKeyEncrypted);
    }

    public boolean hasSignatureResponse() {
        return this.signatureByte.length != 0;
    }

    public boolean verifySignature(@NotNull PublicKey connectionPublickey, @NotNull PublicKey signaturePublicKey, byte[] data) {
        try {
            return CryptUtils.verifyData(ByteUtils.concatByteArrays(connectionPublickey.getEncoded(), data), signatureByte, signaturePublicKey);
        } catch (Exception ignored) {
            return false;
        }
    }

}
