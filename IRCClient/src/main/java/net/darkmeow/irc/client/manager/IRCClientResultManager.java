package net.darkmeow.irc.client.manager;

import net.darkmeow.irc.client.data.IRCResultSendMessageToPrivate;
import net.darkmeow.irc.client.enums.EnumDisconnectType;
import net.darkmeow.irc.client.enums.EnumResultLogin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

public class IRCClientResultManager {
    public CountDownLatch handShakeLatch = new CountDownLatch(1);

    public Consumer<EnumResultLogin> loginResultCallback;
    public Consumer<IRCResultSendMessageToPrivate> privateResultCallback;

    @NotNull
    public EnumDisconnectType disconnectType = EnumDisconnectType.OTHER;
    @Nullable
    public String disconnectReason;
    public boolean disconnectLogout = false;

    public void reset() {
        this.handShakeLatch = new CountDownLatch(1);
        this.loginResultCallback = null;
        this.privateResultCallback = null;
        this.disconnectType = EnumDisconnectType.OTHER;
        this.disconnectReason = null;
        this.disconnectLogout = false;
    }
}
