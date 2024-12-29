package net.darkmeow.irc.client.manager;

import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function3;
import net.darkmeow.irc.client.result.EnumResultLogin;

public class IRCClientResultManager {
    public Function1<EnumResultLogin, Boolean> loginResultCallback;
    public Function3<String, String, Boolean, Boolean> privateResultCallback;

    public void reset() {
        this.loginResultCallback = null;
        this.privateResultCallback = null;
    }
}
