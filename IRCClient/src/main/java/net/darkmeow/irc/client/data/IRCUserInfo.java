package net.darkmeow.irc.client.data;

import net.darkmeow.irc.data.GameInfoData;

public class IRCUserInfo {

    public final String name;

    public final String rank;

    public IRCUserInfo(String name, String rank) {
        this.name = name;
        this.rank = rank;
    }
}
