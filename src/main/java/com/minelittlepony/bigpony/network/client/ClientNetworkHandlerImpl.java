package com.minelittlepony.bigpony.network.client;

import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import com.minelittlepony.bigpony.BigPony;
import com.minelittlepony.bigpony.Permissions;
import com.minelittlepony.bigpony.Scaling;
import com.minelittlepony.bigpony.data.EntityScale;
import com.minelittlepony.bigpony.network.ConsentPacket;
import com.minelittlepony.bigpony.network.InteractionManager;
import com.minelittlepony.bigpony.network.Network;

import net.fabricmc.fabric.api.client.networking.v1.ClientLoginConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;

public class ClientNetworkHandlerImpl extends InteractionManager {
    private long lastSettingsUpdate = 0;
    private Optional<ConsentPacket> serverConsent = Optional.empty();
    private final Minecraft client = Minecraft.getInstance();

    public ClientNetworkHandlerImpl() {
        Network.SERVER_CONSENT.receiver().addPersistentListener((_, packet) -> {
            log("[C] Got server settings update packet " + packet);
            updateConsent(packet);
        });
        Network.OTHER_PLAYER_SIZE.receiver().addPersistentListener((sender, packet) -> {
            if (sender.level().getEntity(packet.entityId()) instanceof LivingEntity target && target instanceof Scaling.Holder holder) {
                log("[C] Got size packet for other entity " + target.getName().getString());
                holder.getScaling().setDimensions(packet.dimensions());
            }
        });
        ClientLoginConnectionEvents.INIT.register((_, _) -> updateConsent(null));
        ClientPlayConnectionEvents.JOIN.register((_, _, client) -> {
            if (client.player instanceof Scaling.Holder holder) {
                log("[C-JOIN] Giving player initial scale");
                Scaling scaling = holder.getScaling();
                scaling.setDimensions(BigPony.getInstance().getConfig().scale.get());
                scaling.markDirty();
            }
        });

        config.scale.onChanged(this::updateClientSize);
    }

    @Override
    protected void onConfigurationChange() {
        super.onConfigurationChange();
        updateClientSize(BigPony.getInstance().getConfig().scale.get());
    }

    private void updateClientSize(EntityScale scale) {
        if (client.player instanceof Scaling.Holder holder && !scale.equals(holder.getScaling().getDimensions())) {
            log("[C-SET] Player scale loaded from config");
            holder.getScaling().setDimensions(scale);
        }
    }

    @Override
    public long getPermissions() {
        return serverConsent.map(ConsentPacket::permissions).orElseGet(() -> Permissions.with(super.getPermissions(), Permissions.FLAG_HITBOX, false));
    }

    @Override
    public float getMinMultiplier() {
        return serverConsent.map(ConsentPacket::minMultiplier).orElseGet(super::getMinMultiplier);
    }

    @Override
    public float getMaxMultiplier() {
        return serverConsent.map(ConsentPacket::maxMultiplier).orElseGet(super::getMaxMultiplier);
    }

    private void updateConsent(@Nullable ConsentPacket consent) {
        lastSettingsUpdate = System.currentTimeMillis();
        serverConsent = Optional.ofNullable(consent);
    }

    @Override
    public long getLastSettingsUpdateTime() {
        return lastSettingsUpdate;
    }

    @Override
    public void sendSizeUpdate(LivingEntity entity, Scaling scaling) {
        if (serverConsent.isPresent()) {
            if (entity == client.player) {
                log("[C-UPD] Sending size update packet to server for " + entity.getName().getString());
                Network.PLAYER_SIZE.sendToServer(scaling.toUpdatePacket(entity.getId()));
            } else {
                super.sendSizeUpdate(entity, scaling);
            }
        }
    }
}
