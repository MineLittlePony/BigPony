package com.minelittlepony.bigpony;

import java.util.UUID;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;

/**
 * Received on the client when the server notifies us of a player's size changing.
 */
public class MsgOtherPlayerSize extends MsgPlayerSize {

    public MsgOtherPlayerSize(UUID sender, Scaling scaling) {
        super(sender, scaling, true);
    }

    public MsgOtherPlayerSize(PacketByteBuf buff) {
        super(buff);
    }

    @Override
    public void handle(PlayerEntity sender) {
        Scaled player = ((Scaled)MinecraftClient.getInstance().world.getPlayerByUuid(playerId));
        if (player != null) {
            player.getScaling().initFrom(scaling);
            player.getScaling().updateConsent(consentcamera, consentHitboxes, scaling.getMaxMultiplier());
        }
    }
}
