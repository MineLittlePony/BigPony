package com.minelittlepony.bigpony.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.minelittlepony.bigpony.BigPony;

import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;

@Mixin(LivingEntityRenderer.class)
public abstract class MixinLivingEntityRenderer<T extends LivingEntity, M extends EntityModel<T>> {

    @Inject(method = "scaleAndTranslate(Lnet/minecraft/entity/LivingEntity;F)F",
            require = 1,
            at = @At(
                value = "INVOKE",
                target = "Lcom/mojang/blaze3d/platform/GlStateManager;scalef(FFF)V",
                shift = Shift.AFTER))
    private void onPrepareScale(LivingEntity entity, float ticks, CallbackInfoReturnable<Float> ci) {
        BigPony.getInstance().onRenderEntity(entity);
    }
}
