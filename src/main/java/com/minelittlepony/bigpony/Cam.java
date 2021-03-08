package com.minelittlepony.bigpony;

import net.minecraft.nbt.CompoundTag;

public class Cam {
    public float distance;
    public float height;

    public Cam(float distance, float height) {
        this.distance = distance;
        this.height = height;
    }

    public Cam(float fill) {
        this.fill(fill);
    }

    public Cam fill(float fill) {
        distance = fill;
        height = fill;
        return this;
    }

    public void fromTag(CompoundTag tag) {
        distance = tag.getFloat("distance");
        height = tag.getFloat("height");
    }

    public CompoundTag toTag(CompoundTag tag) {
        tag.putFloat("distance", distance);
        tag.putFloat("height", height);
        return tag;
    }
}
