package com.minelittlepony.bigpony.mod;

public class PlayerScaleM implements IPlayerScale {

    private float xScale;
    private float yScale;
    private float zScale;

    public PlayerScaleM(float xScale, float yScale, float zScale) {
        set(xScale, yScale, zScale);
    }

    public void set(float xScale, float yScale, float zScale) {
        this.xScale = xScale;
        this.yScale = yScale;
        this.zScale = zScale;
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

}
