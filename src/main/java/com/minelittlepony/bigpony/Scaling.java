package com.minelittlepony.bigpony;

import com.minelittlepony.bigpony.client.BigPonyClient;
import com.minelittlepony.bigpony.data.BodyScale;
import com.minelittlepony.bigpony.data.EntityScale;
import com.minelittlepony.bigpony.minelittlepony.PresetDetector;
import com.minelittlepony.bigpony.network.MsgPlayerSize;
import com.minelittlepony.bigpony.network.Network;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;

public class Scaling {
    private EntityScale dimensions = EntityScale.DEFAULT;

    private boolean isPony;
    private boolean dirty;

    private long lastSettingsUpdateTime;

    public EntityScale getDimensions() {
        return dimensions;
    }

    public void setDimensions(EntityScale dimensions) {
        if (!this.dimensions.equals(dimensions)) {
            this.dimensions = dimensions;
            markDirty();
        }
    }

    public BodyScale getRenderedBodyScale() {
        return (dimensions.visual() || !isPony) ? dimensions.body() : BodyScale.DEFAULT;
    }

    public EntityDimensions getReplacementSize(PlayerEntity entity, EntityPose pose, EntityDimensions existing) {
        long permissions = InteractionManager.getInstance().getPermissions();
        boolean changeHitbox = Permissions.hitbox(permissions);
        boolean changeCamera = Permissions.camera(permissions);
        return new EntityDimensions(
                changeHitbox ? Math.max(0.04F, multiply(existing.width(), dimensions.body().x())) : existing.width(),
                changeHitbox ? Math.max(0.04F, multiply(existing.height(), dimensions.body().y())) : existing.height(),
                changeCamera ? Math.max(0.004F, multiply(existing.eyeHeight(), dimensions.camera().height())) : existing.eyeHeight(),
                existing.attachments(),
                false
        );
    }

    public float getShadowScale() {
        return Math.min(getRenderedBodyScale().shadowScale(), InteractionManager.getInstance().getMaxMultiplier());
    }

    public float getCameraDistanceMultiplier() {
        return Permissions.camera(InteractionManager.getInstance().getPermissions()) ?  Math.min(dimensions.camera().distance(), InteractionManager.getInstance().getMaxMultiplier()) : 1;
    }

    public void markDirty() {
        dirty = true;
    }

    public void tick(PlayerEntity entity) {
        isPony = PresetDetector.getInstance().isPony(entity);

        long lastSettingsUpdateTime = InteractionManager.getInstance().getLastSettingsUpdateTime();
        if (lastSettingsUpdateTime != this.lastSettingsUpdateTime) {
            dirty = true;
            this.lastSettingsUpdateTime = lastSettingsUpdateTime;
        }

        if (dirty) {
            dirty = false;
            entity.calculateDimensions();
            if (entity instanceof ServerPlayerEntity) {
                Network.OTHER_PLAYER_SIZE.sendToSurroundingPlayers(toUpdatePacket(entity), entity);
            } else if (entity.getWorld().isClient && BigPonyClient.isClientPlayer(entity)) {
                Network.PLAYER_SIZE.sendToServer(toUpdatePacket(entity));
            }
        }
    }

    public MsgPlayerSize toUpdatePacket(Entity owner) {
        return new MsgPlayerSize(owner.getId(), dimensions, true);
    }

    private static float multiply(float existing, float multiplier) {
        return existing * Math.min(multiplier, InteractionManager.getInstance().getMaxMultiplier());
    }

    public interface Holder {
        Scaling getScaling();
    }
}
