package com.minelittlepony.bigpony.mod.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.minelittlepony.bigpony.mod.LiteModBigPony;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;

@Mixin(Render.class)
public abstract class MixinRender<T extends Entity> {
  
  @Shadow
  protected float shadowSize;
  
  @Shadow
  private void renderShadow(Entity entityIn, double x, double y, double z, float shadowAlpha, float partialTicks) {}
  
  @Redirect(method = "doRenderShadowAndFire(Lnet/minecraft/entity/Entity;DDDFF)V",
           at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/entity/Render;renderShadow(Lnet/minecraft/entity/Entity;DDDFF)V"))
  private void redirectRenderShadow(Render<T> instance, Entity entity, double x, double y, double z, float opacity, float ticks) {
      GlStateManager.pushMatrix();
      // The shadows render a tiny bit off the ground, so let's shift it down so as not to cut through people's legs.
      GlStateManager.translate(0, -0.01F, 0);
      float rawShadowSize = shadowSize;
      shadowSize = LiteModBigPony.instance().getUpdatedShadowSize(rawShadowSize, entity);
      renderShadow(entity, x, y, z, opacity, ticks);
      shadowSize = rawShadowSize;
      GlStateManager.popMatrix();
  }
}
