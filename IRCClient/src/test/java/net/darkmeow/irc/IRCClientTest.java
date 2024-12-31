package net.darkmeow.irc;

import net.darkmeow.irc.client.IRCClient;
import net.darkmeow.irc.client.data.IRCUserInfo;
import net.darkmeow.irc.client.enums.EnumPremium;
import net.darkmeow.irc.client.enums.EnumResultLogin;
import net.darkmeow.irc.client.listener.IRCClientListenableProvide;
import net.darkmeow.irc.data.ClientBrandData;
import org.junit.jupiter.api.Test;

public class IRCClientTest {

    @Test
    public void test() {
        IRCClient client = new IRCClient(new IRCClientListenableProvide() {
            @Override
            public void onUpdateUserInfo(String name, String rank, EnumPremium premium) {

            }

            @Override
            public void onMessagePublic(IRCUserInfo sender, String message) {

            }

            @Override
            public void onMessagePrivate(IRCUserInfo sender, String message) {

            }

            @Override
            public void onMessageSystem(String message) {

            }

            @Override
            public void onDisconnect(String message) {

            }
        });
        client.connect("45.207.199.139", 48088, "publicIRCTest123");
        client.login("NekoCurit", "114514", new ClientBrandData("DarkMeow", "114514", 0), result -> {
            if (result == EnumResultLogin.SUCCESS) {
                client.sendMessage("Test");
            }
        });
        try { Thread.sleep(5000); } catch (InterruptedException ignored) {}

    }
}
