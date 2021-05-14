package com.minelittlepony.bigpony;

import com.minelittlepony.bigpony.client.BigPonyClient;
import com.minelittlepony.bigpony.minelittlepony.PresetDetector;

import net.minecraft.entity.EntityPose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.entity.EntityDimensions;

public class Scaling {

    protected Triple body = new Triple(1);

    protected Cam camera = new Cam(1);

    protected float maxMultiplier = 2;

    protected boolean visual = true;

    private transient EntityDimensions knownVanillaSize = PlayerEntity.STANDING_DIMENSIONS;
    private transient EntityDimensions calculatedSize;

    private transient boolean configured;
    private transient boolean dirty;

    private transient boolean serverConsentChanged;
    private transient boolean serverConsentCamera;
    private transient boolean serverConsentHitbox;

    private transient boolean isPony;

    public Scaling(Triple body, Cam camera) {
        this.body = body;
        this.camera = camera;
    }

    public void setVisual(boolean visual) {
        if (visual != this.visual) {
            this.visual = visual;
            markDirty();
        }
    }

    public boolean isVisual() {
        return visual;
    }

    public void setScale(Triple scale) {
        if (!this.body.equals(scale)) {
            this.body = scale;

            markDirty();
        }
    }

    public void setCamera(Cam camera) {
        if (this.camera.height != camera.height || this.camera.distance != camera.distance) {
            this.camera = camera;
            markDirty();
        }
    }

    public float setHeight(float height) {
        if (camera.height != height) {
            camera.height = height;
            markDirty();
        }
        return camera.height;
    }

    public float setDistance(float distance) {
        if (distance != camera.distance) {
            camera.distance = distance;
            markDirty();
        }
        return camera.distance;
    }

    public float getMaxMultiplier() {
        return maxMultiplier;
    }

    public Triple getScale() {
        return body;
    }

    public Triple getVisualScale() {
        return visual || !isPony ? body : Triple.DEFAULT;
    }

    public Cam getCamera() {
        return camera;
    }

    public EntityDimensions getReplacementSize(PlayerEntity entity, EntityPose pose, EntityDimensions existing) {

        if (!(entity instanceof ServerPlayerEntity || serverConsentHitbox)) {
            return existing;
        }
        // This ends up changing hitboxes.
        if (pose == EntityPose.CROUCHING || pose == EntityPose.STANDING) {
            if (calculatedSize == null || existing.height != knownVanillaSize.height || existing.width != knownVanillaSize.width) {
                knownVanillaSize = EntityDimensions.fixed(existing.width, existing.height);
                calculatedSize = EntityDimensions.changing(
                        Math.max(0.25F, multiply(knownVanillaSize.width, body.x)),
                        Math.max(0.14F, multiply(knownVanillaSize.height, body.y))
                );
            }
            return calculatedSize;
        }

        return existing;
    }

    public float getShadowScale() {
        return Math.min(Math.max(getVisualScale().x, getVisualScale().z), maxMultiplier);
    }

    public double getCameraDistance(double existing) {
        return canAlterCamera() ? multiply(existing, camera.distance) : existing;
    }

    public float getReplacementActiveEyeHeight(EntityPose pose, EntityDimensions size, float existing) {
        return getReplacementPassiveEyeHeight(pose, size, existing);
    }

    public float getReplacementPassiveEyeHeight(EntityPose pose, EntityDimensions size, float existing) {
        return canAlterCamera() ? Math.max(0.14F, multiply(existing, camera.height)) : existing;
    }

    private boolean canAlterCamera() {
        return serverConsentCamera && !(!visual && isPony && PresetDetector.getInstance().isFillyCam());
    }

    private float multiply(float existing, float multiplier) {
        return existing * Math.min(multiplier, maxMultiplier);
    }

    private double multiply(double existing, float multiplier) {
        return existing * Math.min(multiplier, maxMultiplier);
    }

    public void markDirty() {
        calculatedSize = null;
        dirty = true;
    }

    public boolean isConfigured() {
        return configured;
    }

    public boolean hasCameraConsent() {
        return serverConsentCamera;
    }

    public boolean hasHitboxConsent() {
        return serverConsentHitbox;
    }

    public void updateConsent(boolean camera, boolean hitbox, float multiplier) {
        serverConsentCamera = camera;
        serverConsentHitbox = hitbox;
        serverConsentChanged = true;
        maxMultiplier = multiplier;
    }

    public void tick(PlayerEntity entity) {

        if (entity.world.isClient) {
            isPony = PresetDetector.getInstance().isPony(entity);
        }

        if (!configured && entity.world.isClient) {
            initFrom(BigPony.getInstance().getScaling());
            Network.CLIENT_UPDATE_PLAYER_SIZE.send(new MsgPlayerSize(entity.getUuid(), this, false));
        }

        if (dirty || serverConsentChanged) {
            serverConsentChanged = false;
            entity.calculateDimensions();
        }

        if (dirty) {
            dirty = false;

            if (entity instanceof ServerPlayerEntity) {
                Network.SERVER_OTHER_PLAYER_SIZE.send(entity.world, new MsgOtherPlayerSize(entity.getUuid(), this));
            } else if (entity.world.isClient && BigPonyClient.isClientPlayer(entity)) {
                Network.CLIENT_UPDATE_PLAYER_SIZE.send(new MsgPlayerSize(entity.getUuid(), this, true));
            }
        }
    }

    public void initFrom(Scaling scale) {
        copyFrom(scale);
        dirty = false;
        configured = true;
    }

    public void copyFrom(Scaling scale) {
        if (scale != this) {
            setScale(scale.getScale());
            setCamera(scale.getCamera());
            setVisual(scale.isVisual());
            maxMultiplier = scale.getMaxMultiplier();
        }
    }

    public void fromTag(NbtCompound tag) {
        camera.fromTag(tag.getCompound("camera"));
        body.fromTag(tag.getCompound("body"));
        configured = true;
        dirty = false;
    }

    public NbtCompound toTag(NbtCompound tag) {
        tag.put("camera", camera.toTag(new NbtCompound()));
        tag.put("body", body.toTag(new NbtCompound()));
        return tag;
    }
}
