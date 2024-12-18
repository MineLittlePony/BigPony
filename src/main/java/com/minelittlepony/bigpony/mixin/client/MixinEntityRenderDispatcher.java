package com.minelittlepony.bigpony.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import com.minelittlepony.bigpony.Scaling;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.world.WorldView;

@Mixin(value = EntityRenderDispatcher.class, priority = 9000 /* apply early so we run before redirects*/)
abstract class MixinEntityRenderDispatcher {
    @ModifyArg(method = "render(Lnet/minecraft/entity/Entity;DDDFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/render/entity/EntityRenderer;)V", at = @At(
                value = "INVOKE",
                target = "net/minecraft/client/render/entity/EntityRenderDispatcher.renderShadow(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/render/entity/state/EntityRenderState;FFLnet/minecraft/world/WorldView;F)V"), index = 6)
    private float modifyRadius(MatrixStack matrices, VertexConsumerProvider vertices, EntityRenderState state, float opacity, float tickDelta, WorldView world, float radius) {
        return radius * (state instanceof Scaling.Holder holder ? holder.getScaling().getShadowScale() : 1);
    }
}
