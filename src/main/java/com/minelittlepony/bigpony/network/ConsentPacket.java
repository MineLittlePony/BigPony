package com.minelittlepony.bigpony.network;

import com.minelittlepony.bigpony.BigPony;
import com.sollace.fabwork.api.packets.Packet;

import net.minecraft.network.PacketByteBuf;

public record ConsentPacket(long permissions, float maxMultiplier) implements Packet {
    public ConsentPacket() {
        this(BigPony.getInstance().getConfig().getPermissions(), BigPony.getInstance().getConfig().maxScalingMultiplier.get());
    }

    public ConsentPacket(PacketByteBuf buffer) {
        this(buffer.readLong(), buffer.readFloat());
    }

    @Override
    public void toBuffer(PacketByteBuf buffer) {
        buffer.writeLong(permissions);
        buffer.writeFloat(maxMultiplier);
    }
}
