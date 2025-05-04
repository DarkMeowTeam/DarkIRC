package net.darkmeow.irc.client;

import net.darkmeow.irc.client.enums.EnumDisconnectType;
import net.darkmeow.irc.client.interfaces.IRCClientProvider;
import net.darkmeow.irc.client.interfaces.manager.IRCSessionManager;
import net.darkmeow.irc.client.listener.IRCClientListenableProvide;
import net.darkmeow.irc.client.manager.SessionManager;
import net.darkmeow.irc.client.network.IRCClientNetworkManager;
import net.darkmeow.irc.client.network.IRCClientOptions;
import net.darkmeow.irc.data.DataUserState;
import net.darkmeow.irc.network.EnumConnectionState;
import net.darkmeow.irc.network.packet.login.c2s.C2SPacketLogin;
import net.darkmeow.irc.network.packet.online.c2s.C2SPacketLogout;
import net.darkmeow.irc.network.packet.online.c2s.C2SPacketMessage;
import net.darkmeow.irc.network.packet.online.c2s.C2SPacketUploadState;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

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

    public IRCClientNetworkManager connection;

    @NotNull
    public final SessionManager sessionManager = new SessionManager();

    @Override
    public boolean connect() {
        connection = IRCClientNetworkManager.createNetworkManagerAndConnect(this, options.host, options.port, options.proxy);

        return isConnected();
    }

    public void closeChannel(EnumDisconnectType type, String reason, boolean logout) {
        if (connection != null) connection.close();
        listenable.onDisconnect(type, reason, logout);
    }

    @Override
    public void disconnect(boolean destroySessionToken) {
        if (isConnected() && connection.getConnectionState() == EnumConnectionState.ONLINE) {
            connection.sendPacket(new C2SPacketLogout(destroySessionToken));
        }
        closeChannel(EnumDisconnectType.DISCONNECT_BY_USER, "", destroySessionToken);
    }

    @Override
    public boolean isConnected() {
        return connection != null && connection.isConnected();
    }

    @Override
    public boolean isLogin() {
        return isConnected() && sessionManager.self != null;
    }

    @Override
    public void login(@NotNull String username, @NotNull String password, boolean invisible) {
        if (isConnected() && connection.getConnectionState() == EnumConnectionState.LOGIN) {
            connection.sendPacket(new C2SPacketLogin(username, password, invisible));
        }
    }

    @Override
    public @NotNull IRCSessionManager getSessionManager() {
        return sessionManager;
    }

    @Override
    public void sendMessageToPublic(@NotNull String message) {
        if (isConnected() && connection.getConnectionState() == EnumConnectionState.ONLINE) {
            connection.sendPacket(new C2SPacketMessage(message));
        }
    }

    @Override
    public void sendMessageToPrivate(@NotNull String receiver, @NotNull String message) {
        if (isConnected() && connection.getConnectionState() == EnumConnectionState.ONLINE) {
            connection.sendPacket(new C2SPacketMessage(message, receiver));
        }
    }

    @Override
    public void sendCommand(@NotNull String root, @NotNull ArrayList<String> args) {
        if (isConnected() && connection.getConnectionState() == EnumConnectionState.ONLINE) {
            connection.sendPacket(new C2SPacketMessage(root, args));
        }
    }

    @Override
    public void uploadState(@NotNull DataUserState state) {
        if (isConnected() && connection.getConnectionState() == EnumConnectionState.ONLINE) {
            connection.sendPacket(new C2SPacketUploadState(state));
        }
    }

}
