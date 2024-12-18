package com.minelittlepony.bigpony.network;

import com.minelittlepony.bigpony.BigPony;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public record ConsentPacket(long permissions, float minMultiplier, float maxMultiplier) {
    public static final PacketCodec<PacketByteBuf, ConsentPacket> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.LONG, ConsentPacket::permissions,
            PacketCodecs.FLOAT, ConsentPacket::minMultiplier,
            PacketCodecs.FLOAT, ConsentPacket::maxMultiplier,
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
