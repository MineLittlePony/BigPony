package com.minelittlepony.bigpony;

import com.google.gson.annotations.Expose;

import net.minecraft.nbt.CompoundTag;

public class Triple {
    @Expose
    public float x;
    @Expose
    public float y;
    @Expose
    public float z;

    public Triple(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Triple(float fill) {
        this.fill(fill);
    }

    public Triple fill(float fill) {
        x = fill;
        y = fill;
        z = fill;
        return this;
    }

    public void fromTag(CompoundTag tag) {
        x = tag.getFloat("x");
        y = tag.getFloat("y");
        z = tag.getFloat("z");
    }

    public CompoundTag toTag(CompoundTag tag) {
        tag.putFloat("x", x);
        tag.putFloat("y", y);
        tag.putFloat("z", z);
        return tag;
    }
}
