package net.darkmeow.irc.client.manager;

import net.darkmeow.irc.client.data.IRCResultSendMessageToPrivate;
import net.darkmeow.irc.client.enums.EnumResultLogin;

import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

public class IRCClientResultManager {
    public CountDownLatch handShakeLatch = new CountDownLatch(1);

    public Consumer<EnumResultLogin> loginResultCallback;
    public Consumer<IRCResultSendMessageToPrivate> privateResultCallback;

    public String disconnectReason = "";

    public void reset() {
        this.handShakeLatch = new CountDownLatch(1);
        this.loginResultCallback = null;
        this.privateResultCallback = null;
        this.disconnectReason = "";
    }
}
