package net.darkmeow.irc.network.handle.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import net.darkmeow.irc.network.EnumConnectionState;
import net.darkmeow.irc.network.EnumPacketDirection;
import net.darkmeow.irc.network.FriendBuffer;
import net.darkmeow.irc.network.IRCNetworkAttributes;
import net.darkmeow.irc.network.packet.Packet;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class NettyPacketDecoder extends ByteToMessageDecoder
{
    @NotNull
    public final EnumPacketDirection direction;

    public NettyPacketDecoder(@NotNull EnumPacketDirection direction)
    {
        this.direction = direction;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() != 0) {
            FriendBuffer packetBuffer = new FriendBuffer(in);
            int packetId = packetBuffer.readVarInt();

            EnumConnectionState connectionState = ctx.channel().attr(IRCNetworkAttributes.PROTOCOL_TYPE).get();
            final Packet packet = connectionState.newPacketClassById(this.direction, packetId, packetBuffer);
            if (packet != null) {
                out.add(packet);
            }
        }
    }

}