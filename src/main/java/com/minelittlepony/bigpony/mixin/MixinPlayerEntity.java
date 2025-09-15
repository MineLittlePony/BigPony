package com.minelittlepony.bigpony.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.minelittlepony.bigpony.Scaling;
import com.minelittlepony.bigpony.data.EntityScale;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;

@Mixin(value = PlayerEntity.class, priority = 1001)
abstract class MixinPlayerEntity extends LivingEntity implements Scaling.Holder {
    private MixinPlayerEntity() {super(null, null);}

    private final Scaling playerScale = new Scaling();

    @ModifyReturnValue(method = "getBaseDimensions(Lnet/minecraft/entity/EntityPose;)Lnet/minecraft/entity/EntityDimensions;", at = @At("RETURN"))
    private EntityDimensions modifyEntityDimensions(EntityDimensions dimensions, EntityPose pose) {
        return getScaling().getReplacementSize((PlayerEntity)(Object)this, pose, dimensions);
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
        getScaling().tick((PlayerEntity)(Object)this);
    }

    @Override
    public Scaling getScaling() {
        return playerScale;
    }
}
