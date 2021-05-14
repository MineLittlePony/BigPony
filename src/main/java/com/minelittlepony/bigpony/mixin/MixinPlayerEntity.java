package com.minelittlepony.bigpony.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.minelittlepony.bigpony.Cam;
import com.minelittlepony.bigpony.Scaled;
import com.minelittlepony.bigpony.Scaling;
import com.minelittlepony.bigpony.Triple;

import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;

@Mixin(PlayerEntity.class)
abstract class MixinPlayerEntity extends LivingEntity implements Scaled {
    private MixinPlayerEntity() {super(null, null);}

    private Scaling playerScale;

    @Inject(method = "getDimensions(Lnet/minecraft/entity/EntityPose;)Lnet/minecraft/entity/EntityDimensions;",
            at = @At("RETURN"),
            cancellable = true)
    protected void redirectGetSize(EntityPose pose, CallbackInfoReturnable<EntityDimensions> info) {
        info.setReturnValue(getScaling().getReplacementSize((PlayerEntity)(Object)this, pose, info.getReturnValue()));
    }

    @Inject(method = "getActiveEyeHeight(Lnet/minecraft/entity/EntityPose;Lnet/minecraft/entity/EntityDimensions;)F",
            at = @At("RETURN"),
            cancellable = true)
    protected void redirectGetActiveEyeHeight(EntityPose pose, EntityDimensions size, CallbackInfoReturnable<Float> info) {
        info.setReturnValue(getScaling().getReplacementActiveEyeHeight(pose, size, info.getReturnValue()));
    }

    @Inject(method = "writeCustomDataToTag(Lnet/minecraft/nbt/NbtCompound;)V", at = @At("HEAD"))
    private void onWriteCustomDataToTag(NbtCompound tag, CallbackInfo info) {
        tag.put("big_pony_data", getScaling().toTag(new NbtCompound()));
    }

    @Inject(method = "readCustomDataFromTag(Lnet/minecraft/nbt/NbtCompound;)V", at = @At("HEAD"))
    private void onReadCustomDataFromTag(NbtCompound tag, CallbackInfo info) {
        if (tag.contains("big_pony_data")) {
            getScaling().fromTag(tag.getCompound("big_pony_data"));
        }
    }

    @Inject(method = "tick()V", at = @At("RETURN"))
    private void afterTick(CallbackInfo info) {
        getScaling().tick((PlayerEntity)(Object)this);
    }

    @Override
    public Scaling getScaling() {
        if (playerScale == null) {
            playerScale = new Scaling(new Triple(1), new Cam(1));
        }
        return playerScale;
    }
}
