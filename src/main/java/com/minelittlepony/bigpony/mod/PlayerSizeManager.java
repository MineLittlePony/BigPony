package com.minelittlepony.bigpony.mod;

import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;

import java.util.Map;
import java.util.UUID;

public class PlayerSizeManager {

    private final GameProfile clientProfile;

    private Map<GameProfile, IPlayerScale> playerSizes = Maps.newHashMap();

    public PlayerSizeManager(GameProfile clientProfile) {
        this.clientProfile = clientProfile;
    }

    void onRenderPlayer(EntityPlayer player) {
        IPlayerScale size = getScale(player.getGameProfile());
        GlStateManager.scale(size.getXScale(), size.getYScale(), size.getZScale());
    }

    private PlayerScale defaultScale(GameProfile profile) {
        return new PlayerScale(1, 1, 1);
    }

    void handlePacket(UUID uuid, IPlayerScale size) {
        EntityPlayer player = Minecraft.getMinecraft().world.getPlayerEntityByUUID(uuid);
        if (player == null) {
            LiteModBigPony.logger.warn("Received scale data for unknown player id " + uuid);
            return;
        }
        GameProfile profile = player.getGameProfile();

        playerSizes.put(profile, size);

        LiteModBigPony.logger.debug("Received scale data for player " + profile.getName());

    }

    void clearPlayers() {
        this.playerSizes.clear();
    }

    public IPlayerScale getScale(GameProfile profile) {
        return playerSizes.computeIfAbsent(profile, this::defaultScale);
    }

    public void setScale(float xScale, float yScale, float zScale) {
        this.playerSizes.put(clientProfile, new PlayerScale(xScale, yScale, zScale));
    }

}
