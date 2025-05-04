package net.darkmeow.irc.data;

import lombok.Getter;
import net.darkmeow.irc.data.enmus.EnumUserPremium;
import org.jetbrains.annotations.NotNull;

public class DataUser {

    @Getter
    @NotNull
    private final String name;

    @Getter
    @NotNull
    private final EnumUserPremium premium;

    @Getter
    private final DataUserState state;

    public DataUser(@NotNull String name, @NotNull EnumUserPremium premium, DataUserState state) {
        this.name = name;
        this.premium = premium;
        this.state = state;
        new DataSkin(new byte[] {}, new byte[] {}, false);
    }

}
