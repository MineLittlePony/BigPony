package com.minelittlepony.bigpony;

import com.minelittlepony.bigpony.data.BodyScale;
import com.minelittlepony.bigpony.data.CameraScale;
import com.minelittlepony.bigpony.data.EntityScale;
import com.minelittlepony.bigpony.minelittlepony.PresetDetector;
import com.minelittlepony.bigpony.network.InteractionManager;
import com.minelittlepony.bigpony.network.MsgPlayerSize;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.player.PlayerEntity;
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

    public BodyScale getHitboxScale() {
        return Permissions.hitbox(InteractionManager.getInstance().getPermissions()) ? dimensions.body() : BodyScale.DEFAULT;
    }

    public CameraScale getCameraScale() {
        return Permissions.hitbox(InteractionManager.getInstance().getPermissions()) ? dimensions.camera() : CameraScale.DEFAULT;
    }

    public EntityDimensions getReplacementSize(PlayerEntity entity, EntityPose pose, EntityDimensions existing) {
        BodyScale hitboxScale = getHitboxScale();
        return new EntityDimensions(
                existing.width() * InteractionManager.getInstance().getClamped(hitboxScale.shadowScale()),
                existing.height() * InteractionManager.getInstance().getClamped(hitboxScale.y()),
                existing.eyeHeight() * InteractionManager.getInstance().getClamped(getCameraScale().height()),
                existing.attachments(),
                false
        );
    }

    public float getShadowScale() {
        return InteractionManager.getInstance().getClamped(getRenderedBodyScale().shadowScale());
    }

    public float getCameraDistanceMultiplier() {
        return Permissions.camera(InteractionManager.getInstance().getPermissions())
                ? InteractionManager.getInstance().getClamped(dimensions.camera().distance())
                : 1;
    }

    public void markDirty() {
        dirty = true;
    }

    public void tick(PlayerEntity entity) {
        isPony = PresetDetector.getInstance().isPony(entity);

        long lastSettingsUpdateTime = InteractionManager.getInstance().getLastSettingsUpdateTime();

        if (dirty || lastSettingsUpdateTime != this.lastSettingsUpdateTime) {
            dirty = false;
            this.lastSettingsUpdateTime = lastSettingsUpdateTime;
            entity.calculateDimensions();
            InteractionManager.getInstance().sendSizeUpdate(entity, this);
        }
    }

    public MsgPlayerSize toUpdatePacket(Entity owner) {
        return new MsgPlayerSize(owner.getId(), dimensions, true);
    }

    public interface Holder {
        Scaling getScaling();
    }
}
