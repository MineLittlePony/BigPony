package com.minelittlepony.bigpony.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.minelittlepony.bigpony.Scaling;
import com.minelittlepony.bigpony.network.Network;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.entity.EntityAccess;

@Mixin(Entity.class)
abstract class MixinEntity implements EntityAccess {
    @Inject(method = "startSeenByPlayer", at = @At("HEAD"))
    private void sendScalingOnStartedTrackingBy(ServerPlayer player, CallbackInfo info) {
        if (this instanceof Scaling.Holder holder) {
            Network.OTHER_PLAYER_SIZE.sendToPlayer(holder.getScaling().toUpdatePacket(getId()), player);
        }
    }
}
