package com.minelittlepony.bigpony.mod;

import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;

import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;

public class PlayerSizeManager {

    private final GameProfile clientProfile;

    private final Map<GameProfile, IPlayerScale> playerSizes = Maps.newHashMap();

    public PlayerSizeManager(GameProfile profile) {
        clientProfile = profile;
    }

    void onRenderPlayer(EntityPlayer player) {
        IPlayerScale size = getScale(player);
        GlStateManager.scale(size.getXScale(), size.getYScale(), size.getZScale());
    }

    private PlayerScale defaultScale() {
        return new PlayerScale(1, 1, 1);
    }

    void handlePacket(UUID uuid, IPlayerScale size) {
        NetworkPlayerInfo player = getPlayer(uuid);
        if (player == null) {
            LiteModBigPony.logger.warn("Received scale data for unknown player id " + uuid);
            return;
        }
        GameProfile profile = player.getGameProfile();

        playerSizes.put(profile, size);

        LiteModBigPony.logger.trace("Received scale data for player " + profile.getName());
    }

    @Nullable
    private NetworkPlayerInfo getPlayer(UUID uuid) {
        NetHandlerPlayClient client = Minecraft.getMinecraft().getConnection();
        return client == null ? null : client.getPlayerInfo(uuid);
    }

    public float getShadowScale(EntityPlayer player) {
        IPlayerScale scale = getScale(player);
        return Math.max(scale.getXScale(), scale.getZScale());
    }

    public IPlayerScale getScale(EntityPlayer player) {
        return playerSizes.computeIfAbsent(player.getGameProfile(), p -> defaultScale());
    }

    public void setOwnScale(float xScale, float yScale, float zScale) {
        playerSizes.put(clientProfile, new PlayerScale(xScale, yScale, zScale));
    }

}
