package com.minelittlepony.bigpony.scale;

import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntitySize;

public interface IPlayerScale {

    void setScale(float x, float y, float z);

    float setXScale(float value);

    float setYScale(float value);

    float setZScale(float value);

    float setDistance(float value);

    float setHeight(float value);

    float getXScale();

    float getYScale();

    float getZScale();

    float getHeight();

    float getDistance();

    void copyFrom(IPlayerScale scale);

    default float getShadowScale() {
        return Math.max(getXScale(), getZScale());
    }

    default double getCameraDistance(double existing) {
        return existing * getDistance();
    }

    EntitySize getReplacementSize(EntityPose pos, EntitySize existing);

    float getReplacementActiveEyeHeight(EntityPose pose, EntitySize size, float existing);

    float getReplacementPassiveEyeHeight(EntityPose pose, EntitySize size, float existing);
}
