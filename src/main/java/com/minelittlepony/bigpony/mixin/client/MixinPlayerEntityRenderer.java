package com.minelittlepony.bigpony.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import com.minelittlepony.bigpony.Scaled;
import com.minelittlepony.bigpony.Scaling;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;

@Mixin(PlayerEntityRenderer.class)
abstract class MixinPlayerEntityRenderer extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
    public MixinPlayerEntityRenderer() {
        super(null, null, 0);
    }

    @ModifyConstant(method = "getPositionOffset", constant = @Constant(doubleValue = -0.125D ))
    private double fixSneakingheight(double offset, AbstractClientPlayerEntity player) {
        Scaling scale = ((Scaled)player).getScaling();
        return offset * scale.getScale().y;
    }
}
