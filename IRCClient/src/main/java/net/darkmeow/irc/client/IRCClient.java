package net.darkmeow.irc.client;

import lombok.Builder;
import net.darkmeow.irc.IRCLib;
import net.darkmeow.irc.client.data.IRCResultSendMessageToPrivate;
import net.darkmeow.irc.client.enums.EnumResultLogin;
import net.darkmeow.irc.client.interfaces.IRCClientProvider;
import net.darkmeow.irc.client.interfaces.manager.IRCSessionManager;
import net.darkmeow.irc.client.listener.IRCClientListenableProvide;
import net.darkmeow.irc.client.manager.IRCClientResultManager;
import net.darkmeow.irc.client.manager.SessionManager;
import net.darkmeow.irc.client.network.IRCClientConnection;
import net.darkmeow.irc.client.network.IRCClientOptions;
import net.darkmeow.irc.data.ClientBrandData;
import net.darkmeow.irc.data.DataSessionOptions;
import net.darkmeow.irc.network.packet.c2s.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class IRCClient implements IRCClientProvider {

    public static @NotNull IRCClientProvider newInstance(@NotNull IRCClientListenableProvide listenable, @NotNull IRCClientOptions options) {
        return new IRCClient(listenable, options);
    }

    @NotNull
    public final IRCClientListenableProvide listenable;

    @NotNull
    public final IRCClientOptions options;

    @Builder(toBuilder = true)
    public IRCClient(@NotNull IRCClientListenableProvide listenable, @NotNull IRCClientOptions options) {
        this.listenable = listenable;
        this.options = options;
    }

    public IRCClientConnection connection = new IRCClientConnection(this);

    @NotNull
    public final SessionManager userManager = new SessionManager();

    @NotNull
    public final IRCClientResultManager resultManager = new IRCClientResultManager();

    @Override
    public boolean connect() {
        resultManager.reset();

        disconnect();

        if (connection.connect(options.host, options.port, options.key, options.proxy)) {
            connection.sendPacket(new C2SPacketHandShake(IRCLib.PROTOCOL_VERSION, options.deviceId), false);

            try {
                if (resultManager.handShakeLatch.await(2, TimeUnit.SECONDS)) {
                    return true;
                }
                disconnect();
            } catch (InterruptedException e) {
                disconnect();
            }
        }

        return false;
    }

    @Override
    public void disconnect() {
        connection.disconnect();
    }

    @Override
    public boolean isConnected() {
        return connection.isConnected();
    }

    @Override
    public boolean isLogin() {
        return connection.isConnected() && userManager.self != null;
    }


    @Override
    public void login(@NotNull String username, @NotNull String password, @NotNull ClientBrandData brand, @Nullable Consumer<EnumResultLogin> callback) {
        if (isConnected()) {
            resultManager.loginResultCallback = callback;

            if (
                !connection.sendPacket(
                    new C2SPacketLogin(username, password, brand),
                    false
                )
            ) {
                if (callback != null) callback.accept(EnumResultLogin.NOT_CONNECT);
            }
        } else {
            if (callback != null) callback.accept(EnumResultLogin.NOT_CONNECT);
        }
    }

    @Override
    public @NotNull IRCSessionManager getSessionManager() {
        return userManager;
    }

    @Override
    public void sendMessageToPublic(@NotNull String message) {
        if (isConnected()) {
            connection.sendPacket(
                new C2SPacketChatPublic(message),
                true
            );
        }
    }

    @Override
    public void sendMessageToPrivate(@NotNull String receiver, @NotNull String message, @Nullable Consumer<IRCResultSendMessageToPrivate> callback) {
        if (isConnected()) {
            resultManager.privateResultCallback = callback;

            connection.sendPacket(
                new C2SPacketChatPrivate(receiver, message),
                true
            );
        }
    }

    @Override
    public void sendCommand(@NotNull String root, @NotNull ArrayList<String> args) {
        if (isConnected()) {
            connection.sendPacket(
                new C2SPacketCommand(root, args),
                true
            );
        }
    }

    @Override
    public void uploadSessionOptions(@NotNull DataSessionOptions options) {
        if (isConnected()) {
            connection.sendPacket(
                new C2SPacketUpdateSessionOptions(options),
                true
            );
        }
    }

    @Override
    public void updatePassword(@NotNull String password) {
        if (isConnected()) {
            connection.sendPacket(
                new C2SPacketChangePassword(password),
                true
            );
        }
    }

}
