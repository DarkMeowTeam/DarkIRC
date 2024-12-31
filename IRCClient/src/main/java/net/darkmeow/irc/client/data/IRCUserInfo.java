package net.darkmeow.irc.client.data;

import net.darkmeow.irc.data.UserInfoData;

public class IRCUserInfo {

    public final String name;

    public final UserInfoData info;

    public IRCUserInfo(String name, UserInfoData info) {
        this.name = name;
        this.info = info;
    }
}
