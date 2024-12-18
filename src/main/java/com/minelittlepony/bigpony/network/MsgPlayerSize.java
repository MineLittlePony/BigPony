package com.minelittlepony.bigpony.network;

import com.minelittlepony.bigpony.data.EntityScale;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

/**
 * Received on the server to notify us when a client user changes their size.
 */
public record MsgPlayerSize(int entityId, EntityScale dimensions, boolean force) {
    public static final PacketCodec<PacketByteBuf, MsgPlayerSize> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER, MsgPlayerSize::entityId,
            EntityScale.PACKET_CODEC, MsgPlayerSize::dimensions,
            PacketCodecs.BOOLEAN, MsgPlayerSize::force,
            MsgPlayerSize::new
    );

    public MsgPlayerSize(PacketByteBuf buff) {
        this(buff.readInt(), EntityScale.PACKET_CODEC.decode(buff), buff.readBoolean());
    }
}
