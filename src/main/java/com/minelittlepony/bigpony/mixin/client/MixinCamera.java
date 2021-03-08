package com.minelittlepony.bigpony.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.minelittlepony.bigpony.Scaled;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;

@Mixin(Camera.class)
abstract class MixinCamera {
    @Inject(method = "method_19318(D)D",
            at = @At("RETURN"),
            cancellable = true)
    private void redirectCameraDistance(double initial, CallbackInfoReturnable<Double> info) {
        info.setReturnValue(((Scaled)MinecraftClient.getInstance().player).getScaling().getCameraDistance(info.getReturnValueD()));
    }
}
