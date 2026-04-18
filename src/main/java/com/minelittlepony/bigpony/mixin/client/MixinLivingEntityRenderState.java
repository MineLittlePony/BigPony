package com.minelittlepony.bigpony.mixin.client;

import org.spongepowered.asm.mixin.Mixin;

import com.minelittlepony.bigpony.client.BigPonyRenderState;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;

@Mixin(value = { LivingEntityRenderState.class, CameraRenderState.class })
abstract class MixinLivingEntityRenderState implements BigPonyRenderState.Holder {
    private final BigPonyRenderState scaling = new BigPonyRenderState(this);

    @Override
    public BigPonyRenderState getBigPonyState() {
        return scaling;
    }
}
