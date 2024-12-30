package net.darkmeow.irc.client;

import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.functions.Function3;
import net.darkmeow.irc.client.data.IRCOtherUserInfo;
import net.darkmeow.irc.client.enums.EnumPremium;
import net.darkmeow.irc.client.enums.EnumResultLogin;
import net.darkmeow.irc.client.listener.IRCClientListenableProvide;
import net.darkmeow.irc.client.manager.IRCClientResultManager;
import net.darkmeow.irc.client.manager.IRCClientUserManager;
import net.darkmeow.irc.client.network.IRCClientConnection;
import net.darkmeow.irc.data.ClientBrandData;
import net.darkmeow.irc.data.GameInfoData;
import net.darkmeow.irc.network.packet.c2s.*;
import net.darkmeow.irc.network.packet.s2c.S2CPacketLoginResult;

import java.net.Proxy;
import java.util.ArrayList;
import java.util.function.Function;

public class IRCClient {

    public IRCClientListenableProvide listenable;

    public IRCClientConnection connection;

    public String name;

    public String rank;

    public EnumPremium premium;

    public final IRCClientUserManager userManager = new IRCClientUserManager();
    public final IRCClientResultManager resultManager = new IRCClientResultManager();

    /**
     * 连接到 IRC 服务器
     *
     * @param host 服务器IP
     * @param port 服务器端口
     * @param key 密钥
     *
     * @return 是否成功
     */
    public boolean connect(String host, int port, String key) {
        return this.connect(host, port, key, Proxy.NO_PROXY);
    }

    /**
     * 连接到 IRC 服务器
     *
     * @param host 服务器IP
     * @param port 服务器端口
     * @param key 密钥
     * @param proxy 代理
     *
     * @return 是否成功
     */
    public boolean connect(String host, int port, String key, Proxy proxy) {
        this.disconnect();

        resultManager.reset();

        connection = new IRCClientConnection(this);
        return connection.connect(host, port, key, proxy);
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
     * @param device 设备码
     * @param brand 客户端信息
     * @param callback 异步执行结果返回
     */
    public void login(String username, String password, String device, ClientBrandData brand, Function1<EnumResultLogin, Boolean> callback) {
        if (isConnected()) {
            resultManager.loginResultCallback = callback;

            connection.sendPacket(
                new C2SPacketLogin(
                    username,
                    password,
                    device,
                    brand
                )
            );
        } else {
            callback.invoke(EnumResultLogin.NOT_CONNECT);
        }
    }

    public void sendMessage(String message) {
        if (isConnected()) {
            connection.sendPacket(
                new C2SPacketChatPublic(message)
            );
        }
    }

    public void sendMessageToPrivate(String user, String message, Function3<String, String, Boolean, Boolean> callback) {
        if (isConnected()) {
            resultManager.privateResultCallback = callback;

            connection.sendPacket(
                new C2SPacketChatPrivate(user, message)
            );
        }
    }

    public void sendCommand(String root, ArrayList<String> args) {
        if (isConnected()) {
            connection.sendPacket(
                new C2SPacketCommand(root, args)
            );
        }
    }

    /**
     * 上报客户端数据
     *
     * @param inGameName 游戏内 ID
     * @param server 当前游玩服务器 IP
     * @param clientFPS 客户端帧率
     * @param attackIRC 是否会攻击 IRC 内成员
     */
    public void postGameInfo(String inGameName, String server, int clientFPS, boolean attackIRC) {
        if (isConnected()) {
            connection.sendPacket(
                new C2SPacketUpdateGameInfo(
                    new GameInfoData(inGameName, server, clientFPS, attackIRC, ClientBrandData.EMPTY)
                )
            );
        }
    }

    /**
     * 异步检查这些玩家 id 内是否包含 irc 内成员
     *
     * @param names 名称列表
     */
    public void queryNamesAsync(ArrayList<String> names) {
        if (isConnected()) {
            connection.sendPacket(
                new C2SPacketQueryUsers(names)
            );
        }
    }

    /**
     * 异步检查这些玩家 id 内是否包含 irc 内成员
     *
     * @param name 名称
     */
    public void queryNamesAsync(String name) {
        if (isConnected()) {
            final ArrayList<String> list = new ArrayList<>();
            list.add(name);

            connection.sendPacket(
                new C2SPacketQueryUsers(list)
            );
        }
    }

}
