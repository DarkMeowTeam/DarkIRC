package net.darkmeow.irc.client.listener;

import net.darkmeow.irc.client.enums.EnumDisconnectType;
import net.darkmeow.irc.client.interfaces.IRCClientProvider;
import net.darkmeow.irc.client.interfaces.data.IRCDataOtherSessionInfo;
import net.darkmeow.irc.client.interfaces.data.IRCDataSelfSessionInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class IRCClientListenableSimple implements IRCClientListenableProvide {

    @Override
    public void onReadyLogin(IRCClientProvider client) {

    }

    @Override
    public void onUpdateSession(@NotNull String token) {

    }

    @Override
    public void onUpdateUserInfo(@NotNull IRCDataSelfSessionInfo info, boolean isFirstLogin) {

    }

    @Override
    public void onMessagePublic(@NotNull IRCDataOtherSessionInfo sender, @NotNull String message) {

    }

    @Override
    public void onMessagePrivate(@NotNull IRCDataOtherSessionInfo sender, @NotNull String message) {

    }

    @Override
    public void onPrivateMessageSendSuccess(@NotNull String receiver, @NotNull String message) {

    }

    @Override
    public void onPrivateMessageSendFailed(@NotNull String receiver) {

    }

    @Override
    public void onMessageSystem(@NotNull String message) {

    }

    @Override
    public void onDisconnect(@NotNull EnumDisconnectType type, @Nullable String reason, boolean logout) {

    }

}
