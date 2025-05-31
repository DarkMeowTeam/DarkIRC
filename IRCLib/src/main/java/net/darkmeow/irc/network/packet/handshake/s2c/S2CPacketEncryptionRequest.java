package net.darkmeow.irc.network.packet.handshake.s2c;

import lombok.Getter;
import net.darkmeow.irc.network.FriendBuffer;
import net.darkmeow.irc.network.packet.S2CPacket;
import net.darkmeow.irc.utils.CryptUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.security.PublicKey;

/**
 * 握手包成功
 * 当客户端接收到此包时 需要立刻开启传输加密 (
 */
public class S2CPacketEncryptionRequest implements S2CPacket {

    @Getter
    @NotNull
    private final PublicKey publicKey;


    @Getter
    @Nullable
    private final String signatureCode;

    public S2CPacketEncryptionRequest(@NotNull PublicKey publicKey) {
        this.publicKey = publicKey;
        this.signatureCode = null;
    }

    public S2CPacketEncryptionRequest(@NotNull PublicKey publicKey, @NotNull String signatureCode) {
        this.publicKey = publicKey;
        this.signatureCode = signatureCode;
    }

    public S2CPacketEncryptionRequest(@NotNull FriendBuffer buffer) {
        this.publicKey = CryptUtils.decodePublicKey(buffer.readByteArray());
        this.signatureCode = buffer.readBoolean() ? buffer.readString(32767) : null;
    }

    @Override
    public void write(@NotNull FriendBuffer buffer) {
        buffer.writeByteArray(this.publicKey.getEncoded());
        if (this.signatureCode != null) {
            buffer.writeBoolean(true);
            buffer.writeString(this.signatureCode);
        } else {
            buffer.writeBoolean(false);
        }
    }

    /**
     * 是否需要验证签名
     */
    public boolean hasSignatureRequire() {
        return this.signatureCode != null;
    }

}
