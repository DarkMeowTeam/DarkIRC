package net.darkmeow.irc.client.listener;

import net.darkmeow.irc.client.enums.EnumDisconnectType;
import net.darkmeow.irc.client.interfaces.IRCClientProvider;
import net.darkmeow.irc.client.interfaces.data.IRCDataOtherSessionInfo;
import net.darkmeow.irc.client.interfaces.data.IRCDataSelfSessionInfo;
import net.darkmeow.irc.network.FriendBuffer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;

public interface IRCClientListenableProvide {
    /**
     * 连接服务器成功 可以开始登录
     */
    void onReadyLogin(IRCClientProvider client);
    /**
     * 接收到新的登录凭据(token)时调用
     * 在这里编写保存凭据部分代码并下次登录使用用户名+凭据登录而不是用户名+密码
     *
     * @param token 凭据
     */
    void onUpdateSession(@NotNull String token);

    /**
     * 当从服务端接收到自身信息时调用
     * 包括 首次登录成功, 收到服务端更新头衔, 权限等级变更..
     *
     * @param info 信息
     * @param isFirstLogin 是否为首次登录成功
     */
    void onUpdateUserInfo(@NotNull IRCDataSelfSessionInfo info, boolean isFirstLogin);
    /**
     * 接收到来自其它会话的皮肤数据时调用
     *
     * @param info 会话信息
     */
    void onUpdateSessionSkin(@NotNull IRCDataOtherSessionInfo info);
    /**
     * 收到公共聊天时调用
     *
     * @param sender 发送者数据
     * @param message 消息内容
     */
    void onMessagePublic(@NotNull IRCDataOtherSessionInfo sender, @NotNull String message);

    /**
     * 收到私有聊天时调用
     *
     * @param sender 发送者数据
     * @param message 消息内容
     */
    void onMessagePrivate(@NotNull IRCDataOtherSessionInfo sender, @NotNull String message);
    /**
     * 收到其它客户端更新输入状态时调用
     *
     * @param publicInputs 正在向公开聊天输入消息的客户端
     * @param privateInputs 正在向私有聊天输入消息的客户端 (仅向当前客户端)
     */
    void onUpdateOtherInputs(@NotNull Set<UUID> publicInputs, @NotNull Set<UUID> privateInputs);
    /**
     * 发送私有聊天成功时调用
     *
     * @param receiver 接收者数据
     * @param message 消息内容
     */
    void onPrivateMessageSendSuccess(@NotNull String receiver, @NotNull String message);
    /**
     * 发送私有聊天失败时调用 (对方不在线)
     *
     * @param receiver 接收者
     */
    void onPrivateMessageSendFailed(@NotNull String receiver);
    /**
     * 收到系统消息调用
     *
     * @param message 消息内容
     */
    void onMessageSystem(@NotNull String message);
    /**
     * 收到自定义通道包
     *
     * @param channel 名称
     * @param data 数据
     */
    void onCustomPayload(@NotNull String channel, @NotNull FriendBuffer data);
    /**
     * 与 IRC 服务器连接中断时调用
     *
     * @param type 类型  意外断开/被服务端踢出/用户主动断开
     * @param reason 原因  只有在被服务端踢出时才有
     * @param logout 退出登录  凭据失效
     */
    void onDisconnect(@NotNull EnumDisconnectType type, @Nullable String reason, boolean logout);

}
