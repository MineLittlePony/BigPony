package com.minelittlepony.bigpony.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.minelittlepony.bigpony.client.BigPonyRenderState;
import com.minelittlepony.bigpony.data.BodyScale;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.phys.Vec3;

@Mixin(AvatarRenderer.class)
abstract class MixinPlayerEntityRenderer {
    @ModifyReturnValue(method = "getRenderOffset", at = @At("RETURN"))
    private Vec3 fixSneakingHeight(Vec3 offset, AvatarRenderState state) {
        if (state instanceof BigPonyRenderState.Holder holder) {
            BodyScale scale = holder.getBigPonyState().bodyScale;
            return offset.multiply(scale.x(), scale.y(), scale.z());
        }
        return offset;
    }
}
