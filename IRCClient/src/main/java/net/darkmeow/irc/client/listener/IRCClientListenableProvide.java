package net.darkmeow.irc.client.listener;

import net.darkmeow.irc.client.data.IRCUserInfo;
import net.darkmeow.irc.client.enums.EnumDisconnectType;
import net.darkmeow.irc.client.enums.EnumPremium;

public interface IRCClientListenableProvide {

    void onUpdateUserInfo(String name, String rank, EnumPremium premium);

    void onMessagePublic(IRCUserInfo sender, String message);

    void onMessagePrivate(IRCUserInfo sender, String message);

    void onMessageSystem(String message);

    void onDisconnect(EnumDisconnectType type, String reason, boolean logout);

}
