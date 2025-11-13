package com.minelittlepony.bigpony.mixin.client;

import org.spongepowered.asm.mixin.Mixin;

import com.minelittlepony.bigpony.client.BigPonyRenderState;

import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;

@Mixin(LivingEntityRenderState.class)
abstract class MixinLivingEntityRenderState extends EntityRenderState implements BigPonyRenderState.Holder {
    private final BigPonyRenderState scaling = new BigPonyRenderState(this);

    @Override
    public BigPonyRenderState getBigPonyState() {
        return scaling;
    }
}
