package com.minelittlepony.bigpony.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record CameraScale(float distance, float height) {
    public static final CameraScale DEFAULT = of(1);
    public static final Codec<CameraScale> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.FLOAT.fieldOf("distance").forGetter(CameraScale::distance),
            Codec.FLOAT.fieldOf("height").forGetter(CameraScale::height)
    ).apply(i, CameraScale::new));
    public static final StreamCodec<FriendlyByteBuf, CameraScale> PACKET_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, (t -> t.distance),
            ByteBufCodecs.FLOAT, (t -> t.height),
            CameraScale::new
    );

    public static CameraScale of(float fill) {
        return new CameraScale(fill, fill);
    }

    public CameraScale withDistance(float distance) {
        return new CameraScale(distance, height);
    }

    public CameraScale withHeight(float height) {
        return new CameraScale(distance, height);
    }
}
