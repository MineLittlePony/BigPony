package com.minelittlepony.bigpony.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.minelittlepony.bigpony.Scaled;
import com.minelittlepony.bigpony.Triple;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;

@Mixin(LivingEntityRenderer.class)
abstract class MixinLivingEntityRenderer<T extends LivingEntity, M extends EntityModel<T>> {
    @Inject(method = "render("
            + "Lnet/minecraft/entity/LivingEntity;"
            + "FF"
            + "Lnet/minecraft/client/util/math/MatrixStack;"
            + "Lnet/minecraft/client/render/VertexConsumerProvider;"
            + "I"
            + ")V",
            require = 1,
            at = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/client/render/entity/LivingEntityRenderer;setupTransforms("
                        + "Lnet/minecraft/entity/LivingEntity;"
                        + "Lnet/minecraft/client/util/math/MatrixStack;"
                        + "FFF"
                        + ")V",
                shift = Shift.AFTER))
    private void onPrepareScale(T entity, float yaw, float tickDelta, MatrixStack stack, VertexConsumerProvider vertices, int lighting, CallbackInfo ci) {
        if (entity instanceof Scaled) {
            Triple scale = ((Scaled)entity).getScaling().getVisualScale();
            stack.scale(scale.x, scale.y, scale.z);
        }
    }
}
