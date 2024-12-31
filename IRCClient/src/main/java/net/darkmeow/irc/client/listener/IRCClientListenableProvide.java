package net.darkmeow.irc.client.listener;

import net.darkmeow.irc.client.data.IRCOtherUserInfo;
import net.darkmeow.irc.client.enums.EnumPremium;

public interface IRCClientListenableProvide {

    void onUpdateUserInfo(String name, String rank, EnumPremium premium);

    void onMessagePublic(IRCOtherUserInfo sender, String message);

    void onMessagePrivate(IRCOtherUserInfo sender,  String message);

    void onMessageSystem(String message);

    void onDisconnect(String message);

}