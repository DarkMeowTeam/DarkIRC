package net.darkmeow.irc.client;

import io.netty.util.AttributeKey;

import java.util.UUID;

public final class AttributeKeys {
    public static final AttributeKey<UUID> UUID = AttributeKey.newInstance("uuid");
}
