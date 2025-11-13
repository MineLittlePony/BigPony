package com.minelittlepony.bigpony.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public record CameraScale(float distance, float height) {
    public static final CameraScale DEFAULT = of(1);
    public static final Codec<CameraScale> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.FLOAT.fieldOf("distance").forGetter(CameraScale::distance),
            Codec.FLOAT.fieldOf("height").forGetter(CameraScale::height)
    ).apply(i, CameraScale::new));
    public static final PacketCodec<PacketByteBuf, CameraScale> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.FLOAT, (t -> t.distance),
            PacketCodecs.FLOAT, (t -> t.height),
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
