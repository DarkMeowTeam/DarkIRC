package net.darkmeow.irc.client.data;

import net.darkmeow.irc.client.enums.EnumPremium;
import net.darkmeow.irc.client.interfaces.data.IRCDataSelfInfo;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class DataSelfInfo implements IRCDataSelfInfo {

    public boolean invalid = false;

    @NotNull
    public UUID uniqueId;

    @NotNull
    public String name;

    @NotNull
    public String rank;

    @NotNull
    public EnumPremium premium;

    public DataSelfInfo(@NotNull UUID clientUniqueId, @NotNull String name, @NotNull String rank, @NotNull EnumPremium premium) {
        this.uniqueId = clientUniqueId;
        this.name = name;
        this.rank = rank;
        this.premium = premium;
    }

    /**
     * 更新 IRCSelfInfo 数据
     *
     * @param name 用户名
     * @param rank 头衔
     * @param premium 权限等级
     */
    public void update(@NotNull String name, @NotNull String rank, @NotNull EnumPremium premium) {
        this.invalid = false;
        this.name = name;
        this.rank = rank;
        this.premium = premium;
    }

    /**
     * 标记 IRCSelfInfo 数据为失效 (连接断开)
     */
    public void markInvalid() {
        this.invalid = true;
    }

    @Override
    public boolean isInvalid() {
        return this.invalid;
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
}
