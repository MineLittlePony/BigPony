package com.minelittlepony.bigpony.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.minelittlepony.bigpony.Scaling;
import com.minelittlepony.bigpony.network.Network;

import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;

@Mixin(Entity.class)
abstract class MixinEntity {
    @Inject(method = "onStartedTrackingBy", at = @At("HEAD"))
    private void sendScalingOnStartedTrackingBy(ServerPlayerEntity player, CallbackInfo info) {
        if (this instanceof Scaling.Holder holder) {
            Network.OTHER_PLAYER_SIZE.sendToPlayer(holder.getScaling().toUpdatePacket((Entity)(Object)this), player);
        }
    }
}
