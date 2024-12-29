package net.darkmeow.irc.client.data;

import net.darkmeow.irc.data.GameInfoData;

public class IRCOtherUserInfo extends IRCUserInfo {

    public final GameInfoData info;

    public IRCOtherUserInfo(String name, String rank, GameInfoData info) {
        super(name, rank);
        this.info = info;
    }
}
