package com.minelittlepony.bigpony.mod;

import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;

import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;

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
        if (client == null)
            return null;
        return client.getPlayerInfo(uuid);
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
