package net.darkmeow.irc.client.interfaces;

import net.darkmeow.irc.client.data.IRCResultSendMessageToPrivate;
import net.darkmeow.irc.client.enums.EnumResultLogin;
import net.darkmeow.irc.client.interfaces.manager.IRCSessionManager;
import net.darkmeow.irc.data.ClientBrandData;
import net.darkmeow.irc.data.DataSessionOptions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.UUID;
import java.util.function.Consumer;

public interface IRCClientProvider {
    /**
     * 尝试连接到 IRC 服务器
     * 会阻塞当前线程
     *
     * @return 是否成功
     */
    boolean connect();
    /**
     * 与 IRC 服务器断开连接
     */
    void disconnect();
    /**
     * 获取 IRC 服务器连接状态
     * 不是登录状态
     *
     * @return 是否已连接到服务器
     */
    boolean isConnected();
    /**
     * 获取 IRC 服务器登录状态
     * 不同于 {@link #isConnected()}, 这里获取的是是否已登录账号而并非与服务端建立连接
     *
     * @return 登录状态
     */
    boolean isLogin();
    /**
     * 登录到 IRC 服务器
     * 注意: 在登录前 请使用 {@link #connect()} 连接
     *
     * @param username 用户名
     * @param password 密码/token
     * @param brand 客户端信息
     * @param invisible 隐身登录
     */
    EnumResultLogin login(@NotNull String username, @NotNull String password, @NotNull ClientBrandData brand, boolean invisible);
    /**
     * 登录到 IRC 服务器 但是不上线
     * 注意: 在登录前 请使用 {@link #connect()} 连接
     *
     * @param username 用户名
     * @param password 密码/token
     * @param brand 客户端信息
     */
    EnumResultLogin loginNotOnline(@NotNull String username, @NotNull String password, @NotNull ClientBrandData brand);
    /**
     * 退出登录(使token失效)
     */
    void logout();
    /**
     * 获取会话管理器
     * 可以通过会话管理器获取已连接到服务器的其它会话和自身会话信息
     *
     * @return 连接管理器
     */
    @NotNull IRCSessionManager getSessionManager();
    /**
     * 向公共聊天频道发送一条消息
     * 注意: 发送消息前 请确保你已经登录 你可以使用 {@link #login(String, String, ClientBrandData, boolean)} 进行登录
     *
     * @param message 消息内容
     */
    void sendMessageToPublic(@NotNull String message);
    /**
     * 向指定用户/会话(Session)发送私聊消息
     * 私聊消息只能被发送方和接收方看到
     * 注意: 发送消息前 请确保你已经登录 你可以使用 {@link #login(String, String, ClientBrandData, boolean)} 进行登录
     *
     * @param receiver 接收方 可以填写用户名(对方所有设备都会收到)或填写指定客户端唯一ID(要想传入指定客户端唯一ID, 请通过{@link UUID#toString()})
     * @param message 消息内容
     * @param callback 发送消息状态回调
     */
    void sendMessageToPrivate(@NotNull String receiver, @NotNull String message, @Nullable Consumer<IRCResultSendMessageToPrivate> callback);
    /**
     * 发送指令到服务端
     * 注意: 发送指令前 请确保你已经登录 你可以使用 {@link #login(String, String, ClientBrandData, boolean)} 进行登录
     *
     * @param root 根指令名 (例如指令 {@code /code wtf} 中 根指令名为 {@code code}  (不包括反斜杠))
     * @param args 指令参数 (例如指令 {@code /code wtf} 中 指令参数为 {@code wtf}  (空格分割))
     */
    void sendCommand(@NotNull String root, @NotNull ArrayList<String> args);
    /**
     * 上报当前数据
     * 上报的数据将会立刻转发给其它在线的会话
     * 执行上报前请确保你已经登录 没有登录就上报的数据将会被忽略
     *
     * @param options 数据
     */
    void uploadSessionOptions(@NotNull DataSessionOptions options);
    /**
     * 更新当前登录账号的密码
     * 更新后所有已登录会话将会被全部断开连接 您需要手动重新登录
     *
     * @param password 新密码
     */
    void updatePassword(@NotNull String password);
}