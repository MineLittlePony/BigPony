package com.minelittlepony.bigpony.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.minelittlepony.bigpony.Scaling;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;

@Mixin(Camera.class)
abstract class MixinCamera {
    @ModifyVariable(method = "clipToSpace(F)F", at = @At("HEAD"), argsOnly = true, index = 1)
    private float adjustDistance(float initial) {
        return (MinecraftClient.getInstance().player instanceof Scaling.Holder holder ? holder.getScaling().getCameraDistanceMultiplier() : 1) * initial;
    }
}
