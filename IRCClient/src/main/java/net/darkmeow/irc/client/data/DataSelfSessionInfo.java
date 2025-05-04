package net.darkmeow.irc.client.data;

import lombok.Getter;
import net.darkmeow.irc.client.interfaces.data.IRCDataSelfSessionInfo;
import net.darkmeow.irc.data.enmus.EnumUserPremium;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public final class DataSelfSessionInfo extends DataSessionInfo implements IRCDataSelfSessionInfo {

    @Getter
    @NotNull
    public String name;

    @Getter
    @NotNull
    public EnumUserPremium premium;

    public boolean invisible;

    public DataSelfSessionInfo(@NotNull UUID clientUniqueId, @NotNull String name, @NotNull EnumUserPremium premium, boolean invisible) {
        super(clientUniqueId);

        this.name = name;
        this.premium = premium;
        this.invisible = invisible;
    }

    /**
     * 更新 IRCSelfInfo 数据
     *
     * @param name 用户名
     * @param premium 权限等级
     */
    public void update(@NotNull String name, @NotNull EnumUserPremium premium, boolean invisible) {
        this.valid = true;
        this.name = name;
        this.premium = premium;
        this.invisible = invisible;
    }

    @Override
    public @NotNull UUID getUniqueId() {
        return this.uniqueId;
    }


    @Override
    public boolean getIsInvisible() {
        return false;
    }

}
