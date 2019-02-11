package com.minelittlepony.bigpony.mod.mixin;

import com.minelittlepony.bigpony.mod.ducks.IEntityRenderer;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.resources.IResourceManagerReloadListener;

import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityRenderer.class)
@Implements(@Interface(iface = IEntityRenderer.class, prefix = "bigpony$"))
public abstract class MixinEntityRenderer implements IResourceManagerReloadListener {

    private static final String DISTANCE = "Lnet/minecraft/client/renderer/EntityRenderer;thirdPersonDistancePrev:F";

    private float thirdPersonDistanceCustom;

    @Redirect(method = "orientCamera(F)V",
              at = @At(value = "FIELD", ordinal = 0, target = DISTANCE, opcode = Opcodes.GETFIELD))
    private float fixCameraDistance(EntityRenderer thus) {
        return thirdPersonDistanceCustom;
    }

    public void bigpony$setThirdPersonDistance(float distance) {
        thirdPersonDistanceCustom = 4 * distance;
    }
}
