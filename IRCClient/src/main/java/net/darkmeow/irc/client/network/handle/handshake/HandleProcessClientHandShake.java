package net.darkmeow.irc.client.network.handle.handshake;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import net.darkmeow.irc.client.enums.EnumDisconnectType;
import net.darkmeow.irc.client.network.IRCClientNetworkManager;
import net.darkmeow.irc.network.EnumConnectionState;
import net.darkmeow.irc.network.packet.handshake.c2s.C2SPacketEncryptionResponse;
import net.darkmeow.irc.network.packet.handshake.s2c.S2CPacketDenyHandShake;
import net.darkmeow.irc.network.packet.handshake.s2c.S2CPacketEncryptionRequest;
import net.darkmeow.irc.network.packet.handshake.s2c.S2CPacketHandShakeSuccess;
import net.darkmeow.irc.utils.CryptUtils;
import org.jetbrains.annotations.NotNull;

import javax.crypto.SecretKey;

public final class HandleProcessClientHandShake extends ChannelInboundHandlerAdapter {

    @NotNull
    public final IRCClientNetworkManager connection;

    public HandleProcessClientHandShake(@NotNull IRCClientNetworkManager connection) {
        this.connection = connection;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object packet) throws Exception {
        if (packet instanceof S2CPacketEncryptionRequest) {
            handleEncryptionRequest((S2CPacketEncryptionRequest) packet);
        } else if (packet instanceof S2CPacketHandShakeSuccess) {
            handleHandShakeSuccess((S2CPacketHandShakeSuccess) packet);
        } else if (packet instanceof S2CPacketDenyHandShake) {
            handleDenyHandShake((S2CPacketDenyHandShake) packet);
        } else {
            super.channelRead(ctx, packet);
        }
    }

    public void handleEncryptionRequest(S2CPacketEncryptionRequest packet) {
        final SecretKey secretkey = CryptUtils.createNewSharedKey();

        this.connection.sendPacket(
            new C2SPacketEncryptionResponse(packet.getPublicKey(), secretkey),
            future -> connection.enableEncryption(secretkey)
        );
    }

    public void handleHandShakeSuccess(S2CPacketHandShakeSuccess packet) {
        this.connection.base.sessionManager.reset(packet.getSessionId());
        this.connection.setConnectionState(EnumConnectionState.LOGIN);
        this.connection.base.listenable.onReadyLogin(this.connection.base);
    }

    public void handleDenyHandShake(S2CPacketDenyHandShake packet) {
        this.connection.base.closeChannel(EnumDisconnectType.KICK_BY_SERVER, packet.getReason(), false);
    }
}
