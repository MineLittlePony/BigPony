package com.minelittlepony.bigpony.network.client;

import com.minelittlepony.bigpony.BigPony;
import com.minelittlepony.bigpony.InteractionManager;
import com.minelittlepony.bigpony.Permissions;
import com.minelittlepony.bigpony.Scaling;
import com.minelittlepony.bigpony.network.ConsentPacket;
import com.minelittlepony.bigpony.network.MsgPlayerSize;
import com.minelittlepony.bigpony.network.Network;

import net.fabricmc.fabric.api.client.networking.v1.ClientLoginConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;

public class ClientNetworkHandlerImpl extends InteractionManager {
    private final MinecraftClient client = MinecraftClient.getInstance();

    private long permissions = Permissions.DEFAULT;
    private float maxScalingMultiplier;

    public ClientNetworkHandlerImpl() {
        Network.SERVER_CONSENT.receiver().addPersistentListener(this::handleConsent);
        Network.OTHER_PLAYER_SIZE.receiver().addPersistentListener(this::handleSizeUpdate);
        ClientLoginConnectionEvents.INIT.register((handler, client) -> {
            permissions = Permissions.DEFAULT;
            maxScalingMultiplier = 2;
            BigPony.LOGGER.info("Resetting registered flag");
        });
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            if (client.player instanceof Scaling.Holder holder) {
                Scaling scaling = holder.getScaling();
                scaling.setDimensions(BigPony.getInstance().getConfig().scale.get());
                scaling.markDirty();
            }
        });
    }

    @Override
    public long getPermissions() {
        return permissions;
    }

    @Override
    public float getMaxMultiplier() {
        return maxScalingMultiplier;
    }

    private void handleConsent(PlayerEntity sender, ConsentPacket packet) {
        permissions = packet.permissions();
        maxScalingMultiplier = packet.maxMultiplier();
    }

    private void handleSizeUpdate(PlayerEntity sender, MsgPlayerSize packet) {
        if (client.world.getEntityById(packet.entityId()) instanceof Scaling.Holder holder) {
            holder.getScaling().setDimensions(packet.dimensions());
        }
    }
}
