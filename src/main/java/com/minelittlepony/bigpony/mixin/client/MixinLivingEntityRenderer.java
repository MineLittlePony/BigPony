package com.minelittlepony.bigpony.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.minelittlepony.bigpony.Scaling;
import com.minelittlepony.bigpony.client.BigPonyRenderState;
import com.minelittlepony.bigpony.data.BodyScale;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;

@Mixin(LivingEntityRenderer.class)
abstract class MixinLivingEntityRenderer<T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>> {
    @Inject(method = "render(Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;Lnet/minecraft/client/render/state/CameraRenderState;)V",
            at = @At(
                value = "INVOKE",
                target = "net/minecraft/client/render/entity/LivingEntityRenderer.setupTransforms(Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;FF)V"))
    private void modifyScaleOnSetupTransforms(S state, MatrixStack matrices, OrderedRenderCommandQueue queue, CameraRenderState camera, CallbackInfo info) {
        if (state instanceof BigPonyRenderState.Holder holder) {
            BodyScale scale = holder.getBigPonyState().bodyScale;
            matrices.scale(scale.x(), scale.y(), scale.z());
        }
    }

    @Inject(method = "updateRenderState", at = @At("HEAD"))
    private void onUpdateRenderState(T entity, S state, float tickDelta, CallbackInfo info) {
        if (entity instanceof Scaling.Holder holder && state instanceof BigPonyRenderState.Holder s) {
            s.getBigPonyState().update(entity, holder.getScaling());
        }
    }
}
