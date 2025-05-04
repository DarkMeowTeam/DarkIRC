package net.darkmeow.irc.network;

import io.netty.util.AttributeKey;

public class IRCNetworkAttributes {
    public static final AttributeKey<EnumConnectionState> PROTOCOL_TYPE = AttributeKey.valueOf("protocol_type");
}
