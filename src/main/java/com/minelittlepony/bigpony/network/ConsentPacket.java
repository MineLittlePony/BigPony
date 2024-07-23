package com.minelittlepony.bigpony.network;

import com.minelittlepony.bigpony.BigPony;
import com.sollace.fabwork.api.packets.Packet;

import net.minecraft.network.PacketByteBuf;

public record ConsentPacket(long permissions, float minMultiplier, float maxMultiplier) implements Packet {
    public ConsentPacket() {
        this(
            BigPony.getInstance().getConfig().getPermissions(),
            BigPony.getInstance().getConfig().minScalingMultiplier.get(),
            BigPony.getInstance().getConfig().maxScalingMultiplier.get()
        );
    }

    public ConsentPacket(PacketByteBuf buffer) {
        this(buffer.readLong(), buffer.readFloat(), buffer.readFloat());
    }

    @Override
    public void toBuffer(PacketByteBuf buffer) {
        buffer.writeLong(permissions);
        buffer.writeFloat(minMultiplier);
        buffer.writeFloat(maxMultiplier);
    }
}
