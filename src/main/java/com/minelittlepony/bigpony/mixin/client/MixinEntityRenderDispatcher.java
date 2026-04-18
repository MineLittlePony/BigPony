package com.minelittlepony.bigpony.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import com.llamalad7.mixinextras.sugar.Local;
import com.minelittlepony.bigpony.client.BigPonyRenderState;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;

@Mixin(value = EntityRenderer.class)
abstract class MixinEntityRenderDispatcher<T extends Entity, S extends EntityRenderState> {
    @ModifyArg(
            method = "extractShadow(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;Lnet/minecraft/client/Minecraft;Lnet/minecraft/world/level/Level;)V",
            at = @At(value = "INVOKE", target = "java/lang/Math.min(FF)F"),
            index = 0)
    private float modifyShadowRadius(float radius, @Local(ordinal = 0) S state) {
        return radius * (state instanceof BigPonyRenderState.Holder holder ? holder.getBigPonyState().bodyScale.shadowScale() : 1);
    }
}
