package com.minelittlepony.bigpony.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.minelittlepony.bigpony.Scaling;
import com.minelittlepony.bigpony.data.BodyScale;
import com.minelittlepony.bigpony.network.InteractionManager;

import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;

@Mixin(LivingEntityRenderer.class)
abstract class MixinLivingEntityRenderer<T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>> {
    @Inject(method = "setupTransforms", at = @At("HEAD"))
    private void modifyScaleOnSetupTransforms(S state, MatrixStack matrices, float animationProgress, float baseScale, CallbackInfo info) {
        if (state instanceof Scaling.Holder holder) {
            BodyScale scale = holder.getScaling().getRenderedBodyScale();
            matrices.scale(
                    InteractionManager.getInstance().getClamped(scale.x()),
                    InteractionManager.getInstance().getClamped(scale.y()),
                    InteractionManager.getInstance().getClamped(scale.z())
            );
        }
    }

    @Inject(method = "updateRenderState", at = @At("HEAD"))
    private void onUpdateRenderState(T entity, S state, float tickDelta, CallbackInfo info) {
        if (entity instanceof Scaling.Holder holder) {
            ((Scaling.MutableHolder)state).setScaling(holder.getScaling());
        }
    }
}
