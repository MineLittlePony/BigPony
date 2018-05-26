package com.minelittlepony.bigpony.mod;

//TODO: Why isn't this a subtype of IPlayerScale?
public interface BigPony {

    void setScale(float xScale, float yScale, float zScale);

    float getxScale();

    float getyScale();

    float getzScale();

    void setHeight(float height);

    float getHeight();

    void setDistance(float distance);

    float getDistance();
}
