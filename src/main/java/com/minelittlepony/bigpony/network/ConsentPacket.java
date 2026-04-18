package com.minelittlepony.bigpony.network;

import com.minelittlepony.bigpony.BigPony;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record ConsentPacket(long permissions, float minMultiplier, float maxMultiplier) {
    public static final StreamCodec<FriendlyByteBuf, ConsentPacket> PACKET_CODEC = StreamCodec.composite(
            ByteBufCodecs.LONG, ConsentPacket::permissions,
            ByteBufCodecs.FLOAT, ConsentPacket::minMultiplier,
            ByteBufCodecs.FLOAT, ConsentPacket::maxMultiplier,
            ConsentPacket::new
    );

    public ConsentPacket() {
        this(
            BigPony.getInstance().getConfig().getPermissions(),
            BigPony.getInstance().getConfig().minScalingMultiplier.get(),
            BigPony.getInstance().getConfig().maxScalingMultiplier.get()
        );
    }
}
