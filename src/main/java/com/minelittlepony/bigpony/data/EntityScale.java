package com.minelittlepony.bigpony.data;

import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public record EntityScale(BodyScale model, Optional<BodyScale> hitbox, CameraScale camera) {
    public static final EntityScale DEFAULT = new EntityScale(BodyScale.DEFAULT, Optional.empty(), CameraScale.DEFAULT);

    public static final Codec<EntityScale> CODEC = RecordCodecBuilder.create(i -> i.group(
            BodyScale.CODEC.fieldOf("body").forGetter(EntityScale::model),
            BodyScale.CODEC.optionalFieldOf("hitbox").forGetter(EntityScale::hitbox),
            CameraScale.CODEC.fieldOf("camera").forGetter(EntityScale::camera)
    ).apply(i, EntityScale::new));
    public static final PacketCodec<PacketByteBuf, EntityScale> PACKET_CODEC = PacketCodec.tuple(
            BodyScale.PACKET_CODEC, EntityScale::model,
            PacketCodecs.optional(BodyScale.PACKET_CODEC), EntityScale::hitbox,
            CameraScale.PACKET_CODEC, EntityScale::camera,
            EntityScale::new
    );

    public EntityScale {
        if (hitbox.isPresent() && hitbox.get().equals(model)) {
            hitbox = Optional.empty();
        }
    }

    public BodyScale body() {
        return hitbox().orElse(model());
    }

    public EntityScale withHitbox(BodyScale hitbox) {
        return new EntityScale(model, Optional.of(hitbox), camera);
    }

    public EntityScale withModel(BodyScale model) {
        return new EntityScale(model, hitbox, camera);
    }

    public EntityScale withCamera(CameraScale camera) {
        return new EntityScale(model, hitbox, camera);
    }
}
