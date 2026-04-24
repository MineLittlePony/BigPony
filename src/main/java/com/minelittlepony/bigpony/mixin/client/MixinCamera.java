package com.minelittlepony.bigpony.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.minelittlepony.bigpony.Scaling;
import com.minelittlepony.bigpony.client.BigPonyRenderState;

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
    private void onExtractRenderState(CameraRenderState state, float tickDelta, CallbackInfo info) {
        if (state instanceof BigPonyRenderState.Holder holder) {
            holder.getBigPonyState().update(entity);
        }
    }

    @ModifyArg(method = "setupPerspective(FFFFF)V", at = @At(
            value = "INVOKE",
            target = "net/minecraft/client/renderer/Projection.setupPerspective(FFFFF)V"
    ), index = 0)
    private float adjustZNearPlane(float zNear) {
        if (entity instanceof Scaling.Holder holder) {
            zNear = Math.min(zNear, zNear * holder.getScaling().getCameraDistanceMultiplier());
        }
        return zNear;
    }
}
