package com.minelittlepony.bigpony.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.PacketByteBuf;

public record BodyScale(float x, float y, float z) {
    public static final BodyScale DEFAULT = of(1);
    public static final Codec<BodyScale> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.FLOAT.fieldOf("x").forGetter(BodyScale::x),
            Codec.FLOAT.fieldOf("y").forGetter(BodyScale::y),
            Codec.FLOAT.fieldOf("z").forGetter(BodyScale::z)
    ).apply(i, BodyScale::new));

    public BodyScale(PacketByteBuf buffer) {
        this(buffer.readFloat(), buffer.readFloat(), buffer.readFloat());
    }

    public void toBuffer(PacketByteBuf buffer) {
        buffer.writeFloat(x);
        buffer.writeFloat(y);
        buffer.writeFloat(z);
    }

    public float shadowScale() {
        return Math.max(x, z);
    }

    public static BodyScale of(float fill) {
        return new BodyScale(fill, fill, fill);
    }

    public BodyScale withX(float x) {
        return new BodyScale(x, y, z);
    }

    public BodyScale withY(float y) {
        return new BodyScale(x, y, z);
    }

    public BodyScale withZ(float z) {
        return new BodyScale(x, y, z);
    }
}
