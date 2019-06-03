package com.minelittlepony.bigpony.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.minelittlepony.bigpony.BigPony;

import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;

@Mixin(EntityRenderer.class)
public abstract class MixinPlayerEntityRenderer<T extends Entity> {

    // shadowRadius
    @Shadow
    protected float field_4673;

    @Shadow
    private void renderShadow(Entity entity, double x, double y, double z, float opacity, float ticks) {}

    @Redirect(method = "postRender(Lnet/minecraft/entity/Entity;DDDFF)V",
            at = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/client/render/entity/EntityRenderer;renderShadow(Lnet/minecraft/entity/Entity;DDDFF)V"))
    private void redirectRenderShadow(EntityRenderer<T> instance, Entity entity, double x, double y, double z, float opacity, float ticks) {
        BigPony.getInstance().onRenderShadow(field_4673, entity, newRadius -> {
            float rawShadowSize = field_4673;
            field_4673 = newRadius;
            renderShadow(entity, x, y, z, opacity, ticks);
            field_4673 = rawShadowSize;
        });
    }
}
