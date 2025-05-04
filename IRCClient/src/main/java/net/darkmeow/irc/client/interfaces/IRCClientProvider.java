package net.darkmeow.irc.client.interfaces;

import net.darkmeow.irc.client.interfaces.manager.IRCSessionManager;
import net.darkmeow.irc.data.DataSkin;
import net.darkmeow.irc.data.DataUserState;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.UUID;

public interface IRCClientProvider {
    /**
     * 尝试连接到 IRC 服务器
     * 会阻塞当前线程
     */
    void connect() throws Throwable;
    /**
     * 与 IRC 服务器断开连接
     *
     * @param destroySessionToken 是否销毁当前登录 token  销毁后下次将无法使用该 token 登录
     */
    void disconnect(boolean destroySessionToken);
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
     * 注意: 推荐在 IRCClientListenableProvide 中 onReadyLogin 中使用
     *
     * @param username 用户名
     * @param password 密码/token
     * @param invisible 隐身登录
     */
    void login(@NotNull String username, @NotNull String password, boolean invisible);
    /**
     * 获取会话管理器
     * 可以通过会话管理器获取已连接到服务器的其它会话和自身会话信息
     *
     * @return 连接管理器
     */
    @NotNull IRCSessionManager getSessionManager();
    /**
     * 向公共聊天频道发送一条消息
     * 注意: 发送消息前 请确保你已经登录
     *
     * @param message 消息内容
     */
    void sendMessageToPublic(@NotNull String message);
    /**
     * 向指定用户/会话(Session)发送私聊消息
     * 私聊消息只能被发送方和接收方看到
     * 注意: 发送消息前 请确保你已经登录
     *
     * @param receiver 接收方 可以填写用户名(对方所有设备都会收到)或填写指定客户端唯一ID(要想传入指定客户端唯一ID, 请通过{@link UUID#toString()})
     * @param message 消息内容
     */
    void sendMessageToPrivate(@NotNull String receiver, @NotNull String message);
    /**
     * 发送指令到服务端
     * 注意: 发送指令前 请确保你已经登录
     *
     * @param root 根指令名 (例如指令 {@code /code wtf} 中 根指令名为 {@code code}  (不包括反斜杠))
     * @param args 指令参数 (例如指令 {@code /code wtf} 中 指令参数为 {@code wtf}  (空格分割))
     */
    void sendCommand(@NotNull String root, @NotNull ArrayList<String> args);
    /**
     * 清空消息发送状态
     */
    void updateInputStatus();
    /**
     * 更新消息发送状态
     * 即将发送到公共聊天
     *
     * @param message 待发送内容 可传入空
     */
    void updateInputStatus(@NotNull String message);
    /**
     * 更新消息发送状态
     * 即将发送到私有聊天
     *
     * @param message 待发送内容 可传入空
     * @param receiver 接收者
     */
    void updateInputStatus(@NotNull String receiver, @NotNull String message);
    /**
     * 上报当前数据
     * 上报的数据将会立刻转发给其它在线的会话
     * 执行上报前请确保你已经登录 没有登录就上报的数据将会被忽略
     *
     * @param options 数据
     */
    void uploadState(@NotNull DataUserState options);
    /**
     * 查询指定会话皮肤数据
     *
     * @param sessionId 会话唯一标识
     */
    void querySkin(@NotNull UUID sessionId);
    /**
     * 上传皮肤
     *
     * @param skin 皮肤数据
     */
    void uploadSkin(@NotNull DataSkin skin);
    /**
     * 修改登录密码
     *
     * @param newPassword 新登录密码
     */
    void updatePassword(@NotNull String newPassword);
}