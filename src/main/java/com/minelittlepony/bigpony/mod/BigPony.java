package com.minelittlepony.bigpony.mod;

public interface BigPony extends IPlayerScale {

    void setScale(float xScale, float yScale, float zScale);

    void setHeight(float height);

    void setDistance(float distance);

    float getDistance();
}
