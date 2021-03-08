package com.minelittlepony.bigpony;

import com.google.gson.annotations.Expose;

import net.minecraft.entity.EntityPose;
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
        this.height = height;
        markDirty();

        return this.height;
    }

    public float setDistance(float distance) {
        this.distance = distance;
        markDirty();

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

    public void copyFrom(Scaling scale) {
        setScale(scale.getScale());
        setHeight(scale.getHeight());
        setDistance(scale.getDistance());
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
    }
}
