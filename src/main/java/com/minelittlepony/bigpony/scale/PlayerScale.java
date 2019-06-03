package com.minelittlepony.bigpony.scale;

import com.google.gson.annotations.Expose;

import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntitySize;

public class PlayerScale implements IPlayerScale {

    @Expose
    protected float xScale = 1F;

    @Expose
    protected float yScale = 1F;

    @Expose
    protected float zScale = 1F;

    @Expose
    protected float height = 1F;

    @Expose
    protected float distance = 1F;

    //private EntitySize knownVanillaSize = PlayerEntity.STANDING_SIZE;
    //private EntitySize calculatedSize;

    public PlayerScale(float xScale, float yScale, float zScale, float height) {
        this.xScale = xScale;
        this.yScale = yScale;
        this.zScale = zScale;
        this.height = height;
    }

    @Override
    public void setScale(float xScale, float yScale, float zScale) {
        if (xScale != this.xScale || yScale != this.yScale || zScale != this.zScale) {
            this.xScale = xScale;
            this.yScale = yScale;
            this.zScale = zScale;

            markDirty();
        }
    }

    @Override
    public float setXScale(float value) {
        xScale = value;
        markDirty();

        return value;
    }

    @Override
    public float setYScale(float value) {
        yScale = value;
        markDirty();

        return value;
    }

    @Override
    public float setZScale(float value) {
        zScale = value;
        markDirty();

        return value;
    }

    @Override
    public float setHeight(float height) {
        this.height = height;
        markDirty();

        return this.height;
    }

    @Override
    public float setDistance(float distance) {
        this.distance = distance;
        markDirty();

        return this.distance;
    }

    @Override
    public float getXScale() {
        return xScale;
    }

    @Override
    public float getYScale() {
        return yScale;
    }

    @Override
    public float getZScale() {
        return zScale;
    }

    @Override
    public float getHeight() {
        return height;
    }

    @Override
    public float getDistance() {
        return distance;
    }

    @Override
    public void copyFrom(IPlayerScale scale) {
        xScale = scale.getXScale();
        yScale = scale.getYScale();
        zScale = scale.getZScale();
        height = scale.getHeight();

        markDirty();
    }

    @Override
    public EntitySize getReplacementSize(EntityPose pose, EntitySize existing) {
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

    @Override
    public float getReplacementActiveEyeHeight(EntityPose pose, EntitySize size, float existing) {
        return Math.max(0.14F, existing * height);
    }

    @Override
    public float getReplacementPassiveEyeHeight(EntityPose pose, EntitySize size, float existing) {
        return Math.max(0.14F, existing * height);
    }

    protected void markDirty() {
        //calculatedSize = null;
    }
}
