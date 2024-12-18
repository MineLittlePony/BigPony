package com.minelittlepony.bigpony.mixin.client;

import org.spongepowered.asm.mixin.Mixin;

import com.minelittlepony.bigpony.Scaling;

import net.minecraft.client.render.entity.state.LivingEntityRenderState;

@Mixin(LivingEntityRenderState.class)
abstract class MixinLivingEntityRenderState implements Scaling.MutableHolder {
    private Scaling scaling = new Scaling();

    @Override
    public Scaling getScaling() {
        return scaling;
    }

    @Override
    public void setScaling(Scaling scaling) {
        this.scaling = scaling;
    }
}
