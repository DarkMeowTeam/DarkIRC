package net.darkmeow.irc.client.network.handle.handshake;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.darkmeow.irc.client.enums.EnumDisconnectType;
import net.darkmeow.irc.client.network.IRCClientNetworkManager;
import net.darkmeow.irc.network.packet.handshake.c2s.C2SPacketSignatureResponse;
import net.darkmeow.irc.network.packet.handshake.s2c.S2CPacketSignatureRequest;
import org.jetbrains.annotations.NotNull;

public final class HandleHandShakeSignatureVerify extends SimpleChannelInboundHandler<S2CPacketSignatureRequest> {

    @NotNull
    public final IRCClientNetworkManager connection;

    public HandleHandShakeSignatureVerify(@NotNull IRCClientNetworkManager connection) {
        this.connection = connection;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, S2CPacketSignatureRequest packet) throws Exception {
        if (connection.base.options.key != null) {
            this.connection.sendPacket(new C2SPacketSignatureResponse(packet.getCode(), connection.base.options.key));
        } else {
            this.connection.base.closeChannel(EnumDisconnectType.FAILED_TO_LOGIN, "未配置签名私钥, 无法通过服务端认证", false);
        }

    }

}
