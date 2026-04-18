package com.minelittlepony.bigpony.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.ModifyReceiver;
import com.minelittlepony.bigpony.Scaling;
import com.minelittlepony.bigpony.data.EntityScale;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

@Mixin(value = LivingEntity.class, priority = 1001)
abstract class MixinLivingEntity extends Entity implements Scaling.Holder {
    private MixinLivingEntity() {super(null, null);}

    private final Scaling scaling = new Scaling();

    @ModifyReceiver(method = "getDimensions(Lnet/minecraft/world/entity/Pose;)Lnet/minecraft/world/entity/EntityDimensions;",
            at = @At(
                value = "INVOKE",
                target = "net/minecraft/world/entity/EntityDimensions.scale(F)Lnet/minecraft/world/entity/EntityDimensions;"))
    private EntityDimensions modifyEntityDimensions(EntityDimensions dimensions, float scale, Pose pose) {
        return getScaling().getReplacementSize(pose, dimensions);
    }

    @Override
    public Scaling getScaling() {
        return scaling;
    }

    @Inject(method = "addAdditionalSaveData(Lnet/minecraft/world/level/storage/ValueOutput;)V", at = @At("HEAD"))
    private void onWriteCustomDataToTag(ValueOutput view, CallbackInfo info) {
        view.store("big_pony_data", EntityScale.CODEC, getScaling().getDimensions());
    }

    @Inject(method = "readAdditionalSaveData(Lnet/minecraft/world/level/storage/ValueInput;)V", at = @At("HEAD"))
    private void onReadCustomDataFromTag(ValueInput view, CallbackInfo info) {
        view.read("big_pony_data", EntityScale.CODEC).ifPresent(getScaling()::setDimensions);
    }

    @Inject(method = "tick()V", at = @At("RETURN"))
    private void afterTick(CallbackInfo info) {
        if (this instanceof Scaling.Holder holder) {
            holder.getScaling().tick((LivingEntity)(Object)this);
        }
    }
}
