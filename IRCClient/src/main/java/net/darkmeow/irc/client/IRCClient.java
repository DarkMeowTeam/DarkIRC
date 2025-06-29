package net.darkmeow.irc.client;

import net.darkmeow.irc.client.enums.EnumDisconnectType;
import net.darkmeow.irc.client.interfaces.IRCClientProvider;
import net.darkmeow.irc.client.interfaces.manager.IRCSessionManager;
import net.darkmeow.irc.client.listener.IRCClientListenableProvide;
import net.darkmeow.irc.client.manager.SessionManager;
import net.darkmeow.irc.client.network.IRCClientNetworkManager;
import net.darkmeow.irc.client.options.IRCClientOptions;
import net.darkmeow.irc.data.DataSkin;
import net.darkmeow.irc.data.DataUserState;
import net.darkmeow.irc.network.EnumConnectionState;
import net.darkmeow.irc.network.FriendBuffer;
import net.darkmeow.irc.network.packet.login.c2s.C2SPacketLogin;
import net.darkmeow.irc.network.packet.online.c2s.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.UUID;

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
    public void connect() throws Throwable {
        connection = IRCClientNetworkManager.createNetworkManagerAndConnect(this, options.host, options.port, options.proxy);
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
            connection.sendPacket(new C2SPacketMessage(receiver, message));
        }
    }

    @Override
    public void sendCommand(@NotNull String root, @NotNull ArrayList<String> args) {
        if (isConnected() && connection.getConnectionState() == EnumConnectionState.ONLINE) {
            connection.sendPacket(new C2SPacketMessage(root, args));
        }
    }

    @Override
    public void updateInputStatus() {
        if (isConnected() && connection.getConnectionState() == EnumConnectionState.ONLINE) {
            connection.sendPacket(new C2SPacketInputStatus());
        }
    }

    @Override
    public void updateInputStatus(@NotNull String message) {
        if (isConnected() && connection.getConnectionState() == EnumConnectionState.ONLINE) {
            connection.sendPacket(new C2SPacketInputStatus(message));
        }
    }

    @Override
    public void updateInputStatus(@NotNull String receiver, @NotNull String message) {
        if (isConnected() && connection.getConnectionState() == EnumConnectionState.ONLINE) {
            connection.sendPacket(new C2SPacketInputStatus(receiver, message));
        }
    }

    @Override
    public void uploadState(@NotNull DataUserState state) {
        if (isConnected() && connection.getConnectionState() == EnumConnectionState.ONLINE) {
            connection.sendPacket(new C2SPacketUploadState(state));
        }
    }

    @Override
    public void querySkin(@NotNull UUID sessionId) {
        if (isConnected() && connection.getConnectionState() == EnumConnectionState.ONLINE) {
            connection.sendPacket(new C2SPacketQuerySkin(sessionId));
        }
    }

    @Override
    public void queryOnlineSessions() {
        if (isConnected() && connection.getConnectionState() == EnumConnectionState.ONLINE) {
            connection.sendPacket(new C2SPacketQuerySessions(false));
        }
    }

    @Override
    public void uploadSkin(@NotNull DataSkin skin) {
        if (isConnected() && connection.getConnectionState() == EnumConnectionState.ONLINE) {
            connection.sendPacket(new C2SPacketUploadSkin(skin));
        }
    }

    @Override
    public void updatePassword(@NotNull String newPassword) {
        if (isConnected() && connection.getConnectionState() == EnumConnectionState.ONLINE) {
            connection.sendPacket(new C2SPacketUpdatePassword(newPassword));
        }
    }

    @Override
    public void sendCustomPayload(@NotNull String channel, @NotNull FriendBuffer data) {
        if (isConnected() && connection.getConnectionState() == EnumConnectionState.ONLINE) {
            connection.sendPacket(new C2SPacketCustomPayload(channel, data));
        }
    }

}
