package com.minelittlepony.bigpony.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.minelittlepony.bigpony.client.BigPonyRenderState;
import com.minelittlepony.bigpony.data.BodyScale;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.util.math.Vec3d;

@Mixin(PlayerEntityRenderer.class)
abstract class MixinPlayerEntityRenderer {
    @ModifyReturnValue(method = "getPositionOffset", at = @At("RETURN"))
    private Vec3d fixSneakingHeight(Vec3d offset, PlayerEntityRenderState state) {
        BodyScale scale = ((BigPonyRenderState.Holder)state).getBigPonyState().bodyScale;
        return offset.multiply(scale.x(), scale.y(), scale.z());
    }
}
