package com.minelittlepony.bigpony.mod.mixin;

import com.minelittlepony.bigpony.mod.LiteModBigPony;
import com.mumfrey.liteloader.core.LiteLoader;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer implements IResourceManagerReloadListener {


    private static final String DISTANCE = "Lnet/minecraft/client/renderer/EntityRenderer;thirdPersonDistancePrev:F";
    @Shadow
    private float thirdPersonDistancePrev;

    @Redirect(method = "orientCamera(F)V", at = @At(value = "FIELD", ordinal = 0, target = DISTANCE))
    private float fixCameraDistance(EntityRenderer thus) {
        return LiteLoader.getInstance().getMod(LiteModBigPony.class).getCameraDistance(thirdPersonDistancePrev);
    }
}
