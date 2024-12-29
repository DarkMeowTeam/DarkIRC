package net.darkmeow.irc.data;

public class GameInfoData {

    public static GameInfoData EMPTY = new GameInfoData("", "", 0, false, ClientBrandData.EMPTY);

    public String inGameName;

    public String server;

    public int clientFPS;

    public boolean attackIRC;

    public ClientBrandData client;

    public GameInfoData(String inGameName, String server, int clientFPS, boolean attackIRC, ClientBrandData client) {
        this.inGameName = inGameName;
        this.server = server;
        this.clientFPS = clientFPS;
        this.attackIRC = attackIRC;
        this.client = client;
    }

}
