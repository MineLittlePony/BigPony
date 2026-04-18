package com.minelittlepony.bigpony.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import com.minelittlepony.bigpony.Scaling;

import net.minecraft.client.Camera;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

@Mixin(Camera.class)
abstract class MixinCamera {
    @Shadow
    private Entity entity;

    @ModifyArg(method = "alignWithEntity(F)V", at = @At(value = "INVOKE", target = "java/lang/Math.max(FF)F"), index = 0)
    private float adjustCameraDistance(float value) {
        return value * bigpony_getDistanceScale(entity);
    }

    @ModifyArg(method = "alignWithEntity(F)V", at = @At(value = "INVOKE", target = "java/lang/Math.max(FF)F"), index = 1)
    private float adjustMountCameraDistance(float value) {
        if (entity.isPassenger() && entity.getVehicle() instanceof LivingEntity l) {
            return value * bigpony_getDistanceScale(l);
        }
        return value * bigpony_getDistanceScale(entity);
    }

    @Unique
    private float bigpony_getDistanceScale(Entity entity) {
        return entity instanceof Scaling.Holder holder ? holder.getScaling().getCameraDistanceMultiplier() : 1;
    }

    @Inject(method = "extractRenderState", at = @At("HEAD"))
    private void onExtractRenderState(final CameraRenderState cameraState, final float cameraEntityPartialTicks) {

    }
}
