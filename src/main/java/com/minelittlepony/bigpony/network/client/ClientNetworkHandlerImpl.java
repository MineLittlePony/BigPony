package com.minelittlepony.bigpony.network.client;

import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import com.minelittlepony.bigpony.BigPony;
import com.minelittlepony.bigpony.InteractionManager;
import com.minelittlepony.bigpony.Scaling;
import com.minelittlepony.bigpony.network.ConsentPacket;
import com.minelittlepony.bigpony.network.Network;

import net.fabricmc.fabric.api.client.networking.v1.ClientLoginConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

public class ClientNetworkHandlerImpl extends InteractionManager {
    private long lastSettingsUpdate = 0;
    private Optional<ConsentPacket> serverConsent = Optional.empty();

    public ClientNetworkHandlerImpl() {
        Network.SERVER_CONSENT.receiver().addPersistentListener((sender, packet) -> {
            updateConsent(packet);
        });
        Network.OTHER_PLAYER_SIZE.receiver().addPersistentListener((sender, packet) -> {
            if (sender.getWorld().getEntityById(packet.entityId()) instanceof Scaling.Holder holder) {
                holder.getScaling().setDimensions(packet.dimensions());
            }
        });
        ClientLoginConnectionEvents.INIT.register((handler, client) -> updateConsent(null));
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
        return serverConsent.map(ConsentPacket::permissions).orElseGet(super::getPermissions);
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
    public boolean isNetworkConnected() {
        return serverConsent.isPresent();
    }
}
