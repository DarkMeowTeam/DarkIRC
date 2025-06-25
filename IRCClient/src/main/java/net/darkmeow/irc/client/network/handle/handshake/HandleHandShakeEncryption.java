package net.darkmeow.irc.client.network.handle.handshake;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.darkmeow.irc.client.enums.EnumDisconnectType;
import net.darkmeow.irc.client.network.IRCClientNetworkManager;
import net.darkmeow.irc.network.packet.handshake.c2s.C2SPacketEncryptionResponse;
import net.darkmeow.irc.network.packet.handshake.s2c.S2CPacketEncryptionRequest;
import net.darkmeow.irc.utils.CryptUtils;
import org.jetbrains.annotations.NotNull;

import javax.crypto.SecretKey;

public final class HandleHandShakeEncryption extends SimpleChannelInboundHandler<S2CPacketEncryptionRequest> {

    @NotNull
    public final IRCClientNetworkManager connection;

    public HandleHandShakeEncryption(@NotNull IRCClientNetworkManager connection) {
        this.connection = connection;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, S2CPacketEncryptionRequest packet) throws Exception {
        final SecretKey secretkey = CryptUtils.createNewSharedKey();

        if (packet.hasSignatureRequire()) {
            if (connection.base.options.clientKey != null) {
                this.connection.sendPacket(
                    new C2SPacketEncryptionResponse(packet.getPublicKey(), secretkey, connection.base.options.clientKey.getKey(), packet.getSignatureData()),
                    future -> connection.enableEncryption(secretkey)
                );
            } else {
                this.connection.base.closeChannel(EnumDisconnectType.FAILED_TO_LOGIN, "未配置签名私钥, 无法通过服务端认证", false);
            }

        } else {
            this.connection.sendPacket(
                new C2SPacketEncryptionResponse(packet.getPublicKey(), secretkey),
                future -> connection.enableEncryption(secretkey)
            );
        }
    }

}
