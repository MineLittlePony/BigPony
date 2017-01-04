package com.minelittlepony.bigpony.mod;

import net.minecraft.network.PacketBuffer;
import playersync.client.api.PacketSerializer;

import javax.annotation.Nonnull;

public interface IPlayerScale {

    float getXScale();

    float getYScale();

    float getZScale();

    class Serializer implements PacketSerializer<IPlayerScale> {

        @Override
        public void serialize(@Nonnull IPlayerScale object, @Nonnull PacketBuffer buf) {
            buf.writeFloat(object.getXScale());
            buf.writeFloat(object.getYScale());
            buf.writeFloat(object.getZScale());
        }

        @Nonnull
        @Override
        public IPlayerScale deserialize(@Nonnull PacketBuffer buf) {
            float x = buf.readFloat();
            float y = buf.readFloat();
            float z = buf.readFloat();
            return new PlayerScale(x, y, z);
        }
    }
}
