package net.darkmeow.irc.client;

import net.darkmeow.irc.IRCLib;
import net.darkmeow.irc.client.data.IRCResultSendMessageToPrivate;
import net.darkmeow.irc.client.enums.EnumPremium;
import net.darkmeow.irc.client.enums.EnumResultLogin;
import net.darkmeow.irc.client.listener.IRCClientListenableProvide;
import net.darkmeow.irc.client.manager.IRCClientResultManager;
import net.darkmeow.irc.client.manager.IRCClientUserManager;
import net.darkmeow.irc.client.network.IRCClientConnection;
import net.darkmeow.irc.data.ClientBrandData;
import net.darkmeow.irc.data.CustomSkinData;
import net.darkmeow.irc.data.GameInfoData;
import net.darkmeow.irc.data.PlayerSessionData;
import net.darkmeow.irc.network.packet.c2s.*;
import net.darkmeow.irc.utils.DeviceUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.Proxy;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class IRCClient {

    @NotNull
    public final IRCClientListenableProvide listenable;

    public IRCClient(@NotNull IRCClientListenableProvide listenable) {
        this.listenable = listenable;
    }

    public IRCClientConnection connection;

    public String name;

    public String rank;

    public EnumPremium premium;

    public final IRCClientUserManager userManager = new IRCClientUserManager();
    public final IRCClientResultManager resultManager = new IRCClientResultManager();

    private ClientBrandData brand;


    /**
     * 连接到 IRC 服务器
     *
     * @param host 服务器IP
     * @param port 服务器端口
     * @param key 密钥
     *
     * @return 是否成功
     */
    public boolean connect(@NotNull String host, int port, @NotNull String key) {
        return this.connect(host, port, key, Proxy.NO_PROXY, DeviceUtils.getDeviceId());
    }

    /**
     * 连接到 IRC 服务器
     *
     * @param host 服务器IP
     * @param port 服务器端口
     * @param key 密钥
     * @param deviceId 设备码
     *
     * @return 是否成功
     */
    public boolean connect(@NotNull String host, int port, @NotNull String key, @NotNull String deviceId) {
        return this.connect(host, port, key, Proxy.NO_PROXY, deviceId);
    }

    /**
     * 连接到 IRC 服务器
     *
     * @param host 服务器IP
     * @param port 服务器端口
     * @param key 密钥
     * @param proxy 代理
     * @param deviceId 设备码
     *
     * @return 是否成功
     */
    public boolean connect(@NotNull String host, int port, @NotNull String key, @NotNull Proxy proxy, @NotNull String deviceId) {
        this.disconnect();

        resultManager.reset();

        connection = new IRCClientConnection(this);

        if (connection.connect(host, port, key, proxy)) {
            connection.sendPacket(new C2SPacketHandShake(IRCLib.PROTOCOL_VERSION, deviceId), false);

            try {
                if (resultManager.handShakeLatch.await(5, TimeUnit.SECONDS)) {
                    return true;
                }
                disconnect();
                return false;
            } catch (InterruptedException e) {
                disconnect();
                return false;
            }
        }
        return false;
    }

    /**
     * 与 IRC 服务器断开连接
     */
    public void disconnect() {
        if (connection != null) {
            connection.disconnect();
        }
    }

    /**
     * 获取连接状态
     */
    public boolean isConnected() {
        if (connection != null) {
            return connection.isConnected();
        } else {
            return false;
        }
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
            this.brand = brand;

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
     * @param session 游戏内 ID
     * @param server 当前游玩服务器 IP
     * @param skin 皮肤数据
     * @param clientFPS 客户端帧率
     * @param attackIRC 是否会攻击 IRC 内成员
     */
    public void postGameInfo(@NotNull PlayerSessionData session, @Nullable String server, @Nullable CustomSkinData skin, int clientFPS, @NotNull String namePrefix, boolean attackIRC) {
        if (isConnected()) {
            connection.sendPacket(
                new C2SPacketUpdateGameInfo(
                    new GameInfoData(
                        session,
                        this.brand,
                        skin,
                        server,
                        clientFPS,
                        namePrefix,
                        attackIRC
                    )
                ),
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
