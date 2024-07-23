package com.minelittlepony.bigpony.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.minelittlepony.bigpony.Scaling;
import com.minelittlepony.bigpony.data.BodyScale;
import com.minelittlepony.bigpony.network.InteractionManager;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.util.math.Vec3d;

@Mixin(PlayerEntityRenderer.class)
abstract class MixinPlayerEntityRenderer {
    @ModifyReturnValue(method = "getPositionOffset", at = @At("RETURN"))
    private Vec3d fixSneakingHeight(Vec3d offset, AbstractClientPlayerEntity player, float f) {
        BodyScale scale = ((Scaling.Holder)player).getScaling().getRenderedBodyScale();
        return offset.multiply(
                InteractionManager.getInstance().getClamped(scale.x()),
                InteractionManager.getInstance().getClamped(scale.y()),
                InteractionManager.getInstance().getClamped(scale.z())
        );
    }
}
