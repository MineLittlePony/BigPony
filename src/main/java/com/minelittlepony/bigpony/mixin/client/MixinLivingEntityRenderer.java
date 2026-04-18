package com.minelittlepony.bigpony.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.minelittlepony.bigpony.Scaling;
import com.minelittlepony.bigpony.client.BigPonyRenderState;
import com.minelittlepony.bigpony.data.BodyScale;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.entity.LivingEntity;

@Mixin(LivingEntityRenderer.class)
abstract class MixinLivingEntityRenderer<T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>> {
    @Inject(method = "submit(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",
            at = @At(
                value = "INVOKE",
                target = "net/minecraft/client/renderer/entity/LivingEntityRenderer.setupRotations(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;FF)V"))
    private void modifyScaleOnSetupTransforms(S state, PoseStack matrices, SubmitNodeCollector queue, CameraRenderState camera, CallbackInfo info) {
        if (state instanceof BigPonyRenderState.Holder holder) {
            BodyScale scale = holder.getBigPonyState().bodyScale;
            matrices.scale(scale.x(), scale.y(), scale.z());
        }
    }

    @Inject(method = "extractRenderState", at = @At("HEAD"))
    private void onUpdateRenderState(T entity, S state, float tickDelta, CallbackInfo info) {
        if (entity instanceof Scaling.Holder holder && state instanceof BigPonyRenderState.Holder s) {
            s.getBigPonyState().update(entity, holder.getScaling());
        }
    }
}
