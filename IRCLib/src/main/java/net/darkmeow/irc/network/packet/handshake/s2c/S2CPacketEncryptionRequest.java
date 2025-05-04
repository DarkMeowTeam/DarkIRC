package net.darkmeow.irc.network.packet.handshake.s2c;

import lombok.Getter;
import net.darkmeow.irc.network.FriendBuffer;
import net.darkmeow.irc.network.packet.S2CPacket;
import net.darkmeow.irc.utils.CryptUtils;
import org.jetbrains.annotations.NotNull;

import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

/**
 * 握手包成功
 * 当客户端接收到此包时 需要立刻开启传输加密 (
 */
public class S2CPacketEncryptionRequest implements S2CPacket {

    @Getter
    @NotNull
    private final PublicKey publicKey;

    public S2CPacketEncryptionRequest(@NotNull PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public S2CPacketEncryptionRequest(@NotNull FriendBuffer buffer) throws NoSuchAlgorithmException, InvalidKeySpecException {
        this.publicKey = CryptUtils.decodePublicKey(buffer.readByteArray());
    }

    @Override
    public void write(@NotNull FriendBuffer buffer) {
        buffer.writeByteArray(publicKey.getEncoded());
    }

}
