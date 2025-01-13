package net.darkmeow.irc.client;

import net.darkmeow.irc.IRCLib;
import net.darkmeow.irc.client.data.IRCResultSendMessageToPrivate;
import net.darkmeow.irc.client.enums.EnumResultLogin;
import net.darkmeow.irc.client.interfaces.IRCClientProvider;
import net.darkmeow.irc.client.interfaces.manager.IRCSessionManager;
import net.darkmeow.irc.client.listener.IRCClientListenableProvide;
import net.darkmeow.irc.client.manager.ResultManager;
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

    /**
     * 创建一个新的 IRCClient 实例
     *
     * @param listenable 事件监听器
     * @param options 连接配置
     *
     * @return IRCClient
     */
    public static @NotNull IRCClientProvider newInstance(@NotNull IRCClientListenableProvide listenable, @NotNull IRCClientOptions options) {
        return new IRCClient(listenable, options);
    }

    @NotNull
    public final IRCClientListenableProvide listenable;

    @NotNull
    public final IRCClientOptions options;

    public IRCClient(@NotNull IRCClientListenableProvide listenable, @NotNull IRCClientOptions options) {
        this.listenable = listenable;
        this.options = options;
    }

    public IRCClientConnection connection = new IRCClientConnection(this);

    @NotNull
    public final SessionManager sessionManager = new SessionManager();

    @NotNull
    public final ResultManager resultManager = new ResultManager();

    @Override
    public boolean connect() {
        resultManager.reset();
        disconnect();

        if (connection.connect(options.host, options.port, options.key, options.proxy)) {
            for (int attempt = 1; attempt <= 3; attempt++) {
                // 神秘问题 小概率收不到 S2CPacketHandShake 但是其他包没问题
                connection.sendPacket(new C2SPacketHandShake(IRCLib.PROTOCOL_VERSION, options.deviceId), false);

                try {
                    if (resultManager.handShakeLatch.await(1, TimeUnit.SECONDS)) {
                        return true;
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            disconnect();
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
        return connection.isConnected() && sessionManager.self != null;
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
        return sessionManager;
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
