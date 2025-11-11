package com.minelittlepony.bigpony.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import com.minelittlepony.bigpony.Scaling;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

@Mixin(value = EntityRenderer.class)
abstract class MixinEntityRenderDispatcher<T extends Entity, S extends EntityRenderState> {
    @ModifyArg(
            method = "updateShadow(Lnet/minecraft/client/render/entity/state/EntityRenderState;Lnet/minecraft/client/MinecraftClient;Lnet/minecraft/world/World;)V",
            at = @At(value = "INVOKE", target = "java/lang/Math.min(FF)F"),
            index = 0)
    private float modifyShadowRadius(float radius, S state, MinecraftClient client, World world) {
        return radius * (state instanceof Scaling.Holder holder ? holder.getScaling().getShadowScale() : 1);
    }
}
