package com.minelittlepony.bigpony.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import com.minelittlepony.bigpony.Scaling;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;

@Mixin(PlayerEntityRenderer.class)
abstract class MixinPlayerEntityRenderer {
    @ModifyConstant(method = "getPositionOffset", constant = @Constant(doubleValue = -0.125D ))
    private double fixSneakingHeight(double offset, AbstractClientPlayerEntity player) {
        return offset * ((Scaling.Holder)player).getScaling().getDimensions().body().y();
    }
}
