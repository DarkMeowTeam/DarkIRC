package net.darkmeow.irc.client.data;

public class IRCResultSendMessageToPrivate {

    public final boolean success;

    public final String toUser;

    public final String message;

    public IRCResultSendMessageToPrivate(boolean success, String toUser, String message) {
        this.success = success;
        this.toUser = toUser;
        this.message = message;
    }
}
