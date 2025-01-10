package net.darkmeow.irc.client.listener;

import net.darkmeow.irc.client.data.IRCUserInfo;
import net.darkmeow.irc.client.enums.EnumDisconnectType;
import net.darkmeow.irc.client.interfaces.data.IRCDataSelfInfo;

public interface IRCClientListenableProvide {

    void onUpdateUserInfo(IRCDataSelfInfo info, boolean isFirstLogin);

    void onMessagePublic(IRCUserInfo sender, String message);

    void onMessagePrivate(IRCUserInfo sender, String message);

    void onMessageSystem(String message);

    void onDisconnect(EnumDisconnectType type, String reason, boolean logout);

}
