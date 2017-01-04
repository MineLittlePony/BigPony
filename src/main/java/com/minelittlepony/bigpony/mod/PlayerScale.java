package com.minelittlepony.bigpony.mod;

public class PlayerScale implements IPlayerScale {

    private final float xScale;
    private final float yScale;
    private final float zScale;

    public PlayerScale(float xScale, float yScale, float zScale) {
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
