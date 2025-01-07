package net.darkmeow.irc.data;

import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class CustomSkinData {
    /**
     * 皮肤
     */
    @Nullable
    public final String skin;

    /**
     * 披风
     */
    @Nullable
    public final String cape;

    /**
     * 皮肤是否为纤细模式 (仅1.8+ minecraft 客户端)
     */
    public final boolean isSlim;

    /**
     * 自定义皮肤数据
     *
     * @param skin 皮肤
     * @param cape 披风
     * @param isSlim 皮肤是否为纤细模式 (仅1.8+ minecraft 客户端)
     */
    public CustomSkinData(@Nullable String skin, @Nullable String cape, boolean isSlim) {
        this.skin = skin;
        this.cape = cape;
        this.isSlim = isSlim;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof CustomSkinData && obj.hashCode() == this.hashCode();
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.skin, this.cape, this.isSlim);
    }

}
