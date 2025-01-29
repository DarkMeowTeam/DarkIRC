package net.darkmeow.irc.client.data;

import net.darkmeow.irc.client.enums.EnumPremium;
import net.darkmeow.irc.client.interfaces.data.IRCDataSelfSessionInfo;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public final class DataSelfSessionInfo extends DataSessionInfo implements IRCDataSelfSessionInfo {

    @NotNull
    public String name;

    @NotNull
    public String rank;

    @NotNull
    public EnumPremium premium;

    public boolean invisible;

    public DataSelfSessionInfo(@NotNull UUID clientUniqueId, @NotNull String name, @NotNull String rank, @NotNull EnumPremium premium, boolean invisible) {
        super(clientUniqueId);

        this.name = name;
        this.rank = rank;
        this.premium = premium;
        this.invisible = invisible;
    }

    /**
     * 更新 IRCSelfInfo 数据
     *
     * @param name 用户名
     * @param rank 头衔
     * @param premium 权限等级
     */
    public void update(@NotNull String name, @NotNull String rank, @NotNull EnumPremium premium, boolean invisible) {
        this.valid = true;
        this.name = name;
        this.rank = rank;
        this.premium = premium;
        this.invisible = invisible;
    }

    @Override
    public @NotNull UUID getUniqueId() {
        return this.uniqueId;
    }

    @Override
    public @NotNull String getName() {
        return this.name;
    }

    @Override
    public @NotNull String getRank() {
        return this.rank;
    }

    @Override
    public @NotNull EnumPremium getPremium() {
        return this.premium;
    }

    @Override
    public boolean getIsInvisible() {
        return false;
    }

}
