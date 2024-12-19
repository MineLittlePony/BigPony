package com.minelittlepony.bigpony.network;

import com.minelittlepony.bigpony.data.EntityScale;
import com.sollace.fabwork.api.packets.Packet;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;

/**
 * Received on the server to notify us when a client user changes their size.
 */
public record MsgPlayerSize<T extends PlayerEntity>(int entityId, EntityScale dimensions, boolean force) implements Packet<T> {
    public MsgPlayerSize(PacketByteBuf buff) {
        this(buff.readInt(), new EntityScale(buff), buff.readBoolean());
    }

    @Override
    public void toBuffer(PacketByteBuf buff) {
        buff.writeInt(entityId);
        dimensions.toBuffer(buff);
        buff.writeBoolean(force);
    }
}
