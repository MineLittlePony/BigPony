package com.minelittlepony.bigpony.network;

import com.minelittlepony.bigpony.data.EntityScale;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

/**
 * Received on the server to notify us when a client user changes their size.
 */
public record MsgPlayerSize(int entityId, EntityScale dimensions, boolean force) {
    public static final StreamCodec<FriendlyByteBuf, MsgPlayerSize> PACKET_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, MsgPlayerSize::entityId,
            EntityScale.PACKET_CODEC, MsgPlayerSize::dimensions,
            ByteBufCodecs.BOOL, MsgPlayerSize::force,
            MsgPlayerSize::new
    );
}
