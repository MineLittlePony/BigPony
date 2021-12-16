package com.minelittlepony.bigpony.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import com.minelittlepony.bigpony.client.BigPonyClient;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.world.WorldView;

@Mixin(value = EntityRenderDispatcher.class, priority = 9000 /* apply early so we run before redirects*/)
abstract class MixinEntityRenderDispatcher {
    @ModifyArg(method = "render("
            + "Lnet/minecraft/entity/Entity;"
            + "DDDFF"
            + "Lnet/minecraft/client/util/math/MatrixStack;"
            + "Lnet/minecraft/client/render/VertexConsumerProvider;"
            + "I"
            + ")V", at = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/client/render/entity/EntityRenderDispatcher;renderShadow("
                        + "Lnet/minecraft/client/util/math/MatrixStack;"
                        + "Lnet/minecraft/client/render/VertexConsumerProvider;"
                        + "Lnet/minecraft/entity/Entity;"
                        + "FF"
                        + "Lnet/minecraft/world/WorldView;"
                        + "F"
                        + ")V"), index = 6)
    private float modifyRadius(MatrixStack matrices, VertexConsumerProvider vertices, Entity entity, float opacity, float tickDelta, WorldView world, float radius) {
        return BigPonyClient.getInstance().onRenderShadow(radius, entity, matrices);
    }
}
