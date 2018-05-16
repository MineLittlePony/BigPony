package com.minelittlepony.bigpony.mod.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.minelittlepony.bigpony.mod.LiteModBigPony;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;

@Mixin(Render.class)
public abstract class MixinRender<T extends Entity> {
  
  @Shadow
  protected float shadowSize;
  protected float rawShadowSize = -1;
  
  // Redirect can't capture locals (wah-wah-waaaah)
  @Inject(method = "renderShadow(Lnet/minecraft/entity/Entity;DDDFF)V",
          at = @At("HEAD"))
  private void onBeforeRenderShadow(Entity entity, double x, double y, double z, float scale, float ticks, CallbackInfo info) {
      rawShadowSize = shadowSize;
      shadowSize = LiteModBigPony.instance().getUpdatedShadowSize(rawShadowSize, entity);
  }
  
  @Inject(method = "renderShadow(Lnet/minecraft/entity/Entity;DDDFF)V",
          at = @At("RETURN"))
  private void onAfterRenderShadow(Entity entity, double x, double y, double z, float scale, float ticks, CallbackInfo info) {
      shadowSize = rawShadowSize;
  }
}
