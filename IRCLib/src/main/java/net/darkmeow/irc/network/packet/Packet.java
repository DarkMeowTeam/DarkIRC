package net.darkmeow.irc.network.packet;

import net.darkmeow.irc.network.FriendBuffer;
import org.jetbrains.annotations.NotNull;

public interface Packet {

    void write(@NotNull FriendBuffer buffer);

}
