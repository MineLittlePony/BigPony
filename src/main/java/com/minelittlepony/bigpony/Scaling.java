package com.minelittlepony.bigpony;

import com.google.gson.annotations.Expose;
import com.minelittlepony.bigpony.client.BigPonyClient;

import net.minecraft.entity.EntityPose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.entity.EntityDimensions;

public class Scaling {

    @Expose
    protected Triple scale = new Triple(1);

    @Expose
    protected float height = 1F;

    @Expose
    protected float distance = 1F;

    //private EntitySize knownVanillaSize = PlayerEntity.STANDING_SIZE;
    //private EntitySize calculatedSize;

    protected boolean configured;
    protected boolean dirty;

    public Scaling(Triple scale, float height, float distance) {
        this.scale = scale;
        this.height = height;
        this.distance = distance;
    }

    public void setScale(Triple scale) {
        if (!this.scale.equals(scale)) {
            this.scale = scale;

            markDirty();
        }
    }

    public float setHeight(float height) {
        if (height != this.height) {
            this.height = height;
            markDirty();
        }
        return this.height;
    }

    public float setDistance(float distance) {
        if (distance != this.distance) {
            this.distance = distance;
            markDirty();
        }
        return this.distance;
    }

    public Triple getScale() {
        return scale;
    }

    public float getHeight() {
        return height;
    }

    public float getDistance() {
        return distance;
    }

    public EntityDimensions getReplacementSize(EntityPose pose, EntityDimensions existing) {
        // This ends up changing hitboxes.
        /*if (pose == EntityPose.SNEAKING || pose == EntityPose.STANDING) {
            if (calculatedSize == null || existing.height != knownVanillaSize.height || existing.width != knownVanillaSize.width) {
                calculatedSize = EntitySize.resizeable(
                        knownVanillaSize.width * xScale,
                        Math.max(0.14F, knownVanillaSize.height * yScale)
                );
                knownVanillaSize = EntitySize.constant(existing.width, existing.height);
            }
            return calculatedSize;
        }*/

        return existing;
    }

    public float getShadowScale() {
        return Math.max(getScale().x, getScale().z);
    }

    public double getCameraDistance(double existing) {
        return existing * getDistance();
    }

    public float getReplacementActiveEyeHeight(EntityPose pose, EntityDimensions size, float existing) {
        return Math.max(0.14F, existing * getHeight());
    }

    public float getReplacementPassiveEyeHeight(EntityPose pose, EntityDimensions size, float existing) {
        return Math.max(0.14F, existing * getHeight());
    }

    public void markDirty() {
        //calculatedSize = null;
        this.dirty = true;
    }

    public boolean isConfigured() {
        return configured;
    }

    public void tick(PlayerEntity entity) {

        if (!configured && entity.world.isClient) {
            System.out.println("[CLIENT] Requesting size for " + entity.getName().asString());
            initFrom(BigPonyClient.getInstance().getScaling());
            Network.CLIENT_UPDATE_PLAYER_SIZE.send(new MsgPlayerSize(entity.getUuid(), this, false));
        }

        if (dirty) {
            dirty = false;
            entity.calculateDimensions();

            if (entity instanceof ServerPlayerEntity) {
                System.out.println("[SERVER] Sending new size for " + entity.getName().asString());
                Network.SERVER_OTHER_PLAYER_SIZE.send(entity.world, new MsgOtherPlayerSize(entity.getUuid(), this));
            } else if (entity.world.isClient && BigPonyClient.isClientPlayer(entity)) {
                System.out.println("[CLIENT] Sending new size for " + entity.getName().asString());
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
            setHeight(scale.getHeight());
            setDistance(scale.getDistance());
        }
    }

    public void fromTag(CompoundTag tag) {
        setHeight(tag.getFloat("height"));
        setDistance(tag.getFloat("distance"));
        scale.fromTag(tag.getCompound("scale"));
        configured = true;
        dirty = false;
    }

    public CompoundTag toTag(CompoundTag tag) {
        tag.putFloat("height", getHeight());
        tag.putFloat("distance", getDistance());
        tag.put("scale", scale.toTag(new CompoundTag()));
        return tag;
    }
}
