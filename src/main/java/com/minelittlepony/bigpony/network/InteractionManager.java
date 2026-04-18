package com.minelittlepony.bigpony.network;

import java.lang.ref.WeakReference;

import org.jetbrains.annotations.Nullable;

import com.minelittlepony.bigpony.BigPony;
import com.minelittlepony.bigpony.BigPonyConfig;
import com.minelittlepony.bigpony.Scaling;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

public class InteractionManager {
    private static InteractionManager INSTANCE = new InteractionManager();

    public static InteractionManager getInstance() {
        return INSTANCE;
    }

    private WeakReference<MinecraftServer> server = new WeakReference<>(null);
    protected final BigPonyConfig config = BigPony.getInstance().getConfig();
    private ConsentPacket serverSettings = new ConsentPacket();

    protected InteractionManager() {
        if (INSTANCE != null) {
            server = INSTANCE.server;
        }
        INSTANCE = this;
    }

    protected void setServer(@Nullable MinecraftServer server) {
        this.server = new WeakReference<>(server);
    }

    protected void onConfigurationChange() {
        ConsentPacket newSettings = new ConsentPacket();
        if (!newSettings.equals(serverSettings)) {
            sendConfigurationChange(newSettings);
        }
    }

    public void sendConfigurationChange(ConsentPacket newSettings) {
        serverSettings = newSettings;
        MinecraftServer server = this.server.get();
        if (server != null) {
            log("[S-SET] Sending settings update packet to all players");
            Network.SERVER_CONSENT.sendToAllPlayers(newSettings, server);
        }
    }

    public long getPermissions() {
        return config.getPermissions();
    }

    public float getMaxMultiplier() {
        return Math.min(200, config.maxScalingMultiplier.get());
    }

    public float getMinMultiplier() {
        return Math.max(0.004F, config.minScalingMultiplier.get());
    }

    public float getClamped(float value) {
        return Mth.clamp(value, getMinMultiplier(), getMaxMultiplier());
    }

    public long getLastSettingsUpdateTime() {
        return 0;
    }

    public void sendSizeUpdate(LivingEntity entity, Scaling scaling) {
        log("[S-UPD] Sending size update packet for " + entity.getName().getString());
        Network.OTHER_PLAYER_SIZE.sendToSurroundingPlayers(scaling.toUpdatePacket(entity.getId()), entity);
    }

    protected void log(String message) {
        if (config.logNetworkEvents.get()) {
            BigPony.LOGGER.info(message);
        }
    }
}
