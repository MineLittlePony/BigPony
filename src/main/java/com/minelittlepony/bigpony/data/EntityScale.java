package com.minelittlepony.bigpony.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public record EntityScale(BodyScale body, CameraScale camera, boolean visual) {
    public static final EntityScale DEFAULT = new EntityScale(BodyScale.DEFAULT, new CameraScale(1), true);

    public static final Codec<EntityScale> CODEC = RecordCodecBuilder.create(i -> i.group(
            BodyScale.CODEC.fieldOf("body").forGetter(EntityScale::body),
            CameraScale.CODEC.fieldOf("camera").forGetter(EntityScale::camera),
            Codec.BOOL.fieldOf("visual").forGetter(EntityScale::visual)
    ).apply(i, EntityScale::new));
    public static final PacketCodec<PacketByteBuf, EntityScale> PACKET_CODEC = PacketCodec.tuple(
            BodyScale.PACKET_CODEC, EntityScale::body,
            CameraScale.PACKET_CODEC, EntityScale::camera,
            PacketCodecs.BOOLEAN, EntityScale::visual,
            EntityScale::new
    );

    public EntityScale withBody(BodyScale body) {
        return new EntityScale(body, camera, visual);
    }

    public EntityScale withCamera(CameraScale camera) {
        return new EntityScale(body, camera, visual);
    }
}
