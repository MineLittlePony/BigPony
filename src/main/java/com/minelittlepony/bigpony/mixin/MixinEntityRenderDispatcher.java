package com.minelittlepony.bigpony.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.minelittlepony.bigpony.BigPony;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.world.WorldView;

@Mixin(EntityRenderDispatcher.class)
abstract class MixinEntityRenderDispatcher {
    @Redirect(method = "render("
            + "Lnet/minecraft/entity/Entity;"
            + "DDDFF"
            + "Lnet/minecraft/client/util/math/MatrixStack;"
            + "Lnet/minecraft/client/render/VertexConsumerProvider;"
            + "I"
            + ")V",
            at = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/client/render/entity/EntityRenderDispatcher;renderShadow("
                        + "Lnet/minecraft/client/util/math/MatrixStack;"
                        + "Lnet/minecraft/client/render/VertexConsumerProvider;"
                        + "Lnet/minecraft/entity/Entity;"
                        + "FF"
                        + "Lnet/minecraft/world/WorldView;"
                        + "F"
                        + ")V"))
    private void redirectRenderShadow(MatrixStack matrices, VertexConsumerProvider vertices, Entity entity, float opacity, float tickDelta, WorldView world, float radius) {
        BigPony.getInstance().onRenderShadow(radius, entity, matrices, newRadius -> {
            renderShadow(matrices, vertices, entity, opacity, tickDelta, world, newRadius);
        });
    }

    @Shadow
    private static void renderShadow(MatrixStack matrices, VertexConsumerProvider vertices, Entity entity, float opacity, float tickDelta, WorldView world, float radius) { }
}
