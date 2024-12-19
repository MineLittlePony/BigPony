package com.minelittlepony.bigpony.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.PacketByteBuf;

public record CameraScale(float distance, float height) {
    public static final CameraScale DEFAULT = new CameraScale(1);
    public static final Codec<CameraScale> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.FLOAT.fieldOf("distance").forGetter(CameraScale::distance),
            Codec.FLOAT.fieldOf("height").forGetter(CameraScale::height)
    ).apply(i, CameraScale::new));

    public CameraScale(PacketByteBuf buffer) {
        this(buffer.readFloat(), buffer.readFloat());
    }

    public void toBuffer(PacketByteBuf buffer) {
        buffer.writeFloat(distance);
        buffer.writeFloat(height);
    }

    public CameraScale(float fill) {
        this(fill, fill);
    }

    public CameraScale withDistance(float distance) {
        return new CameraScale(distance, height);
    }

    public CameraScale withHeight(float height) {
        return new CameraScale(distance, height);
    }
}
