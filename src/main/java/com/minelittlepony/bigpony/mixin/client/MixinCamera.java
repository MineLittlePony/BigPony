package com.minelittlepony.bigpony.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import com.minelittlepony.bigpony.Scaling;

import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

@Mixin(Camera.class)
abstract class MixinCamera {
    @Shadow
    private Entity focusedEntity;

    @ModifyArg(method = "update(Lnet/minecraft/world/BlockView;Lnet/minecraft/entity/Entity;ZZF)V", at = @At(value = "INVOKE", target = "java/lang/Math.max(FF)F"), index = 0)
    private float adjustCameraDistance(float value) {
        return value * bigpony_getDistanceScale(focusedEntity);
    }

    @ModifyArg(method = "update(Lnet/minecraft/world/BlockView;Lnet/minecraft/entity/Entity;ZZF)V", at = @At(value = "INVOKE", target = "java/lang/Math.max(FF)F"), index = 1)
    private float adjustVehicleCameraDistance(float value) {
        if (focusedEntity.hasVehicle() && focusedEntity.getVehicle() instanceof LivingEntity l) {
            return value * bigpony_getDistanceScale(l);
        }
        return value * bigpony_getDistanceScale(focusedEntity);
    }

    @Unique
    private float bigpony_getDistanceScale(Entity entity) {
        return entity instanceof Scaling.Holder holder ? holder.getScaling().getCameraDistanceMultiplier() : 1;
    }
}
