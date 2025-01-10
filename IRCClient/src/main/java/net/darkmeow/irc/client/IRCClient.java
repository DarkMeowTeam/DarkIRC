package net.darkmeow.irc.client;

import lombok.Builder;
import net.darkmeow.irc.IRCLib;
import net.darkmeow.irc.client.data.IRCResultSendMessageToPrivate;
import net.darkmeow.irc.client.enums.EnumResultLogin;
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

public class IRCClient {

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


    /**
     * 连接到 IRC 服务器
     *
     * @return 是否成功
     */
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

    /**
     * 与 IRC 服务器断开连接
     */
    public void disconnect() {
        connection.disconnect();
    }

    /**
     * 获取连接状态
     */
    public boolean isConnected() {
        return connection.isConnected();
    }

    /**
     * 登录到 IRC 服务器
     *
     * @param username 用户名
     * @param password 密码
     * @param brand 客户端信息
     * @param callback 异步执行结果返回
     */
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

    public void sendMessage(@NotNull String message) {
        if (isConnected()) {
            connection.sendPacket(
                new C2SPacketChatPublic(message),
                true
            );
        }
    }

    public void sendMessageToPrivate(@NotNull String user, @NotNull String message, @Nullable Consumer<IRCResultSendMessageToPrivate> callback) {
        if (isConnected()) {
            resultManager.privateResultCallback = callback;

            connection.sendPacket(
                new C2SPacketChatPrivate(user, message),
                true
            );
        }
    }

    public void sendCommand(@NotNull String root, @NotNull ArrayList<String> args) {
        if (isConnected()) {
            connection.sendPacket(
                new C2SPacketCommand(root, args),
                true
            );
        }
    }

    /**
     * 上报客户端数据
     *
     * @param options 数据
     */
    public void postOptions(@NotNull DataSessionOptions options) {
        if (isConnected()) {
            connection.sendPacket(
                new C2SPacketUpdateSessionOptions(options),
                true
            );
        }
    }

    /**
     * 更新当前登录账号的密码
     * 更新后客户端将登陆失效了需要重新登录
     *
     * @param password 新密码
     */
    public void changePassword(@NotNull String password) {
        if (isConnected()) {
            connection.sendPacket(
                new C2SPacketChangePassword(password),
                true
            );
        }
    }

}
