package net.darkmeow.irc.client.manager;

import net.darkmeow.irc.client.data.IRCResultSendMessageToPrivate;
import net.darkmeow.irc.client.enums.EnumResultLogin;

import java.util.function.Consumer;

public class IRCClientResultManager {
    public Consumer<EnumResultLogin> loginResultCallback;
    public Consumer<IRCResultSendMessageToPrivate> privateResultCallback;

    public String disconnectReason = "";

    public void reset() {
        this.loginResultCallback = null;
        this.privateResultCallback = null;
        this.disconnectReason = "";
    }
}
