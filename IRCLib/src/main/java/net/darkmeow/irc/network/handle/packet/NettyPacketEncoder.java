package net.darkmeow.irc.network.handle.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import net.darkmeow.irc.network.EnumConnectionState;
import net.darkmeow.irc.network.EnumPacketDirection;
import net.darkmeow.irc.network.FriendBuffer;
import net.darkmeow.irc.network.IRCNetworkAttributes;
import net.darkmeow.irc.network.packet.Packet;
import org.jetbrains.annotations.NotNull;

public class NettyPacketEncoder extends MessageToByteEncoder<Packet> {

    @NotNull
    public final EnumPacketDirection direction;

    public NettyPacketEncoder(@NotNull EnumPacketDirection direction)
    {
        this.direction = direction;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Packet packet, ByteBuf out) throws Exception {
        EnumConnectionState state = ctx.channel().attr(IRCNetworkAttributes.PROTOCOL_TYPE).get();

        if (state == null) {
            throw new Exception("Attribute " + IRCNetworkAttributes.PROTOCOL_TYPE.name() + " is null.");
        } else {
            int id = state.getPacketId(this.direction, packet);

            FriendBuffer buffer = new FriendBuffer(out);
            buffer.writeVarInt(id);
            packet.write(buffer);
        }
    }
}
