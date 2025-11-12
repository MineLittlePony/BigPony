package com.minelittlepony.bigpony.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.ModifyReceiver;
import com.minelittlepony.bigpony.Scaling;
import com.minelittlepony.bigpony.data.EntityScale;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;

@Mixin(value = LivingEntity.class, priority = 1001)
abstract class MixinLivingEntity extends Entity implements Scaling.Holder {
    private MixinLivingEntity() {super(null, null);}

    private final Scaling scaling = new Scaling();

    @ModifyReceiver(method = "getDimensions(Lnet/minecraft/entity/EntityPose;)Lnet/minecraft/entity/EntityDimensions;",
            at = @At(
                value = "INVOKE",
                target = "net/minecraft/entity/EntityDimensions.scaled(F)Lnet/minecraft/entity/EntityDimensions;"))
    private EntityDimensions modifyEntityDimensions(EntityDimensions dimensions, float scale, EntityPose pose) {
        return getScaling().getReplacementSize(pose, dimensions);
    }

    @Override
    public Scaling getScaling() {
        return scaling;
    }

    @Inject(method = "writeCustomData(Lnet/minecraft/storage/WriteView;)V", at = @At("HEAD"))
    private void onWriteCustomDataToTag(WriteView view, CallbackInfo info) {
        view.put("big_pony_data", EntityScale.CODEC, getScaling().getDimensions());
    }

    @Inject(method = "readCustomData(Lnet/minecraft/storage/ReadView;)V", at = @At("HEAD"))
    private void onReadCustomDataFromTag(ReadView view, CallbackInfo info) {
        view.read("big_pony_data", EntityScale.CODEC).ifPresent(getScaling()::setDimensions);
    }

    @Inject(method = "tick()V", at = @At("RETURN"))
    private void afterTick(CallbackInfo info) {
        if (this instanceof Scaling.Holder holder) {
            holder.getScaling().tick((LivingEntity)(Object)this);
        }
    }
}
