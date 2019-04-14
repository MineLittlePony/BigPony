package com.minelittlepony.bigpony;

public class PlayerScale implements IPlayerScale {

    private float xScale;
    private float yScale;
    private float zScale;
    private float height;

    public PlayerScale(float xScale, float yScale, float zScale, float height) {
        this.xScale = xScale;
        this.yScale = yScale;
        this.zScale = zScale;
        this.height = height;
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

    public void copyFrom(IPlayerScale scale) {
        this.xScale = scale.getXScale();
        this.yScale = scale.getYScale();
        this.zScale = scale.getZScale();
        this.height = scale.getHeight();
    }

    public void setHeight(float height) {
        this.height = height;
    }
}
