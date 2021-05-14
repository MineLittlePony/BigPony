package com.minelittlepony.bigpony;

import net.minecraft.nbt.NbtCompound;

public class Triple {
    static final Triple DEFAULT = new Triple(1);

    public float x;
    public float y;
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

    public void fromTag(NbtCompound tag) {
        x = tag.getFloat("x");
        y = tag.getFloat("y");
        z = tag.getFloat("z");
    }

    public NbtCompound toTag(NbtCompound tag) {
        tag.putFloat("x", x);
        tag.putFloat("y", y);
        tag.putFloat("z", z);
        return tag;
    }
}
