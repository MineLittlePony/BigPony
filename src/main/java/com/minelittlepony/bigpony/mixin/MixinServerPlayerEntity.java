package com.minelittlepony.bigpony.mixin;

import org.spongepowered.asm.mixin.Mixin;

import com.minelittlepony.bigpony.Scaling;
import com.minelittlepony.bigpony.network.Network;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandlerListener;
import net.minecraft.server.network.ServerPlayerEntity;

@Mixin(ServerPlayerEntity.class)
abstract class MixinServerPlayerEntity extends PlayerEntity implements ScreenHandlerListener {
    MixinServerPlayerEntity() { super(null, null, 0, null);}

    @Override
    public void onStartedTrackingBy(ServerPlayerEntity player) {
        Network.OTHER_PLAYER_SIZE.sendToPlayer(((Scaling.Holder)this).getScaling().toUpdatePacket(this), player);
    }
}
