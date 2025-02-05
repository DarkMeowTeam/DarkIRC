package net.darkmeow.irc.client.manager;

import net.darkmeow.irc.client.data.IRCResultSendMessageToPrivate;
import net.darkmeow.irc.client.enums.EnumDisconnectType;
import net.darkmeow.irc.client.enums.EnumResultLogin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

public final class ResultManager {
    public CountDownLatch handShakeLatch = new CountDownLatch(1);

    @NotNull
    public CountDownLatch loginLatch = new CountDownLatch(1);
    @NotNull
    public EnumResultLogin loginResult = EnumResultLogin.TIME_OUT;

    public Consumer<IRCResultSendMessageToPrivate> privateResultCallback;

    @NotNull
    public EnumDisconnectType disconnectType = EnumDisconnectType.OTHER;
    @Nullable
    public String disconnectReason;
    public boolean disconnectLogout = false;

    public void reset() {
        this.handShakeLatch = new CountDownLatch(1);
        this.loginLatch = new CountDownLatch(1);
        this.loginResult = EnumResultLogin.TIME_OUT;
        this.privateResultCallback = null;
        this.disconnectType = EnumDisconnectType.OTHER;
        this.disconnectReason = null;
        this.disconnectLogout = false;
    }
}
