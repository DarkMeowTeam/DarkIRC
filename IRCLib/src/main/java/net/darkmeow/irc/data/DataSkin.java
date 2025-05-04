package net.darkmeow.irc.data;

import lombok.Getter;

public class DataSkin {

    public static DataSkin EMPTY = new DataSkin(new byte[] {}, new byte[] {}, false);

    @Getter
    private final byte[] skin;

    @Getter
    private final byte[] cape;

    @Getter
    private final boolean slim;

    public DataSkin(byte[] skin, byte[] cape, boolean slim) {
        this.skin = skin;
        this.cape = cape;
        this.slim = slim;
    }
}
