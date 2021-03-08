package com.minelittlepony.bigpony;

import com.google.gson.annotations.Expose;

public class Triple {
    @Expose
    public float x;
    @Expose
    public float y;
    @Expose
    public float z;

    public Triple(float fill) {
        this.fill(fill);
    }

    public Triple fill(float fill) {
        x = fill;
        y = fill;
        z = fill;
        return this;
    }
}
