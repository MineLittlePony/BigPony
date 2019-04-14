package com.minelittlepony.bigpony;

import net.minecraft.network.PacketBuffer;
import playersync.client.api.PacketSerializer;

import javax.annotation.Nonnull;

public interface IPlayerScale {

    float getXScale();

    float getYScale();

    float getZScale();

    float getHeight();

    void copyFrom(IPlayerScale scale);

    void setHeight(float height);

    class Serializer implements PacketSerializer<IPlayerScale> {

        @Override
        public void serialize(@Nonnull IPlayerScale object, @Nonnull PacketBuffer buf) {
            buf.writeFloat(object.getXScale());
            buf.writeFloat(object.getYScale());
            buf.writeFloat(object.getZScale());
            buf.writeFloat(object.getHeight());
        }

        @Nonnull
        @Override
        public IPlayerScale deserialize(@Nonnull PacketBuffer buf) {
            float x = buf.readFloat();
            float y = buf.readFloat();
            float z = buf.readFloat();
            float h = buf.readFloat();
            return new PlayerScale(x, y, z, h);
        }
    }
}
