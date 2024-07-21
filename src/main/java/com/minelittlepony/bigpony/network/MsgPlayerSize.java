package com.minelittlepony.bigpony.network;

import com.minelittlepony.bigpony.data.EntityScale;
import com.sollace.fabwork.api.packets.Packet;

import net.minecraft.network.PacketByteBuf;

/**
 * Received on the server to notify us when a client user changes their size.
 */
public record MsgPlayerSize(int entityId, EntityScale dimensions, boolean force) implements Packet {
    public MsgPlayerSize(PacketByteBuf buff) {
        this(buff.readInt(), EntityScale.PACKET_CODEC.decode(buff), buff.readBoolean());
    }

    @Override
    public void toBuffer(PacketByteBuf buff) {
        buff.writeInt(entityId);
        EntityScale.PACKET_CODEC.encode(buff, dimensions);
        buff.writeBoolean(force);
    }
}
