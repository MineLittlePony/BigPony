package com.minelittlepony.bigpony.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.minelittlepony.bigpony.Scaled;
import com.minelittlepony.bigpony.Triple;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;

@Mixin(LivingEntityRenderer.class)
abstract class MixinLivingEntityRenderer<T extends LivingEntity, M extends EntityModel<T>> {
    @Inject(method = "Lnet/minecraft/client/render/entity/LivingEntityRenderer;setupTransforms("
                        + "Lnet/minecraft/entity/LivingEntity;"
                        + "Lnet/minecraft/client/util/math/MatrixStack;"
                        + "FFF"
                        + ")V",
            at = @At("RETURN"))
    private void onSetupTransforms(T entity, MatrixStack matrices, float animationProgress, float bodyYaw, float tickDelta, CallbackInfo info) {
        if (entity instanceof Scaled) {
            Triple scale = ((Scaled)entity).getScaling().getVisualScale();
            matrices.scale(scale.x, scale.y, scale.z);
        }
    }
}
