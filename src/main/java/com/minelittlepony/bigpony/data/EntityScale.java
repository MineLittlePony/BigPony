package com.minelittlepony.bigpony.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.PacketByteBuf;

public record EntityScale(BodyScale body, CameraScale camera, boolean visual) {
    public static final EntityScale DEFAULT = new EntityScale(BodyScale.DEFAULT, new CameraScale(1), true);

    public static final Codec<EntityScale> CODEC = RecordCodecBuilder.create(i -> i.group(
            BodyScale.CODEC.fieldOf("body").forGetter(EntityScale::body),
            CameraScale.CODEC.fieldOf("camera").forGetter(EntityScale::camera),
            Codec.BOOL.fieldOf("visual").forGetter(EntityScale::visual)
    ).apply(i, EntityScale::new));
    public EntityScale(PacketByteBuf buffer) {
        this(new BodyScale(buffer), new CameraScale(buffer), buffer.readBoolean());
    }

    public void toBuffer(PacketByteBuf buffer) {
        body.toBuffer(buffer);
        camera.toBuffer(buffer);
        buffer.writeBoolean(visual);
    }

    public EntityScale withBody(BodyScale body) {
        return new EntityScale(body, camera, visual);
    }

    public EntityScale withCamera(CameraScale camera) {
        return new EntityScale(body, camera, visual);
    }
}
