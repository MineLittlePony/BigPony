package com.minelittlepony.bigpony.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.minelittlepony.bigpony.Scaled;
import com.minelittlepony.bigpony.Scaling;
import com.minelittlepony.bigpony.Triple;

import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

@Mixin(PlayerEntity.class)
abstract class MixinPlayerEntity extends LivingEntity implements Scaled {
    private MixinPlayerEntity() {super(null, null);}

    private Scaling playerScale;

    @Inject(method = "getSize(Lnet/minecraft/entity/EntityPose;)Lnet/minecraft/entity/EntitySize;",
            at = @At("RETURN"),
            cancellable = true)
    protected void redirectGetSize(EntityPose pose, CallbackInfoReturnable<EntityDimensions> info) {
        info.setReturnValue(getScaling().getReplacementSize(pose, info.getReturnValue()));
    }

    @Inject(method = "getActiveEyeHeight(Lnet/minecraft/entity/EntityPose;Lnet/minecraft/entity/EntitySize;)F",
            at = @At("RETURN"),
            cancellable = true)
    protected void redirectGetActiveEyeHeight(EntityPose pose, EntityDimensions size, CallbackInfoReturnable<Float> info) {
        info.setReturnValue(getScaling().getReplacementActiveEyeHeight(pose, size, info.getReturnValue()));
    }

    @Override
    public Scaling getScaling() {
        if (playerScale == null) {
            playerScale = new Scaling(new Triple(1), 1, 1);
        }
        return playerScale;
    }

    @Override
    public void setScale(Scaling scale) {
        playerScale = scale;
        calculateDimensions();
    }
}
