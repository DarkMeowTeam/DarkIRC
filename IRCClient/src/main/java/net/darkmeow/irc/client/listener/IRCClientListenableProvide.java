package net.darkmeow.irc.client.listener;

import net.darkmeow.irc.client.enums.EnumDisconnectType;
import net.darkmeow.irc.client.interfaces.data.IRCDataOtherSessionInfo;
import net.darkmeow.irc.client.interfaces.data.IRCDataSelfSessionInfo;

public interface IRCClientListenableProvide {

    void onUpdateSession(String token);

    void onUpdateUserInfo(IRCDataSelfSessionInfo info, boolean isFirstLogin);

    void onMessagePublic(IRCDataOtherSessionInfo sender, String message);

    void onMessagePrivate(IRCDataOtherSessionInfo sender, String message);

    void onMessageSystem(String message);

    void onDisconnect(EnumDisconnectType type, String reason, boolean logout);

}
