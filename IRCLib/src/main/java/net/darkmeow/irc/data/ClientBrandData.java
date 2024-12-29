package net.darkmeow.irc.data;

public class ClientBrandData {

    public static ClientBrandData EMPTY = new ClientBrandData("", "", 0);

    public final String id;

    public final String hash;

    public final int version;

    public ClientBrandData(String id, String hash, int version) {
        this.id = id;
        this.hash = hash;
        this.version = version;
    }
}
