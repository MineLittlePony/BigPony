package com.minelittlepony.bigpony.mod.mixin;

import com.minelittlepony.bigpony.mod.LiteModBigPony;
import com.mumfrey.liteloader.core.LiteLoader;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderLivingBase.class)
public abstract class MixinRenderLivingBase<T extends EntityLivingBase> extends Render<T> {

    protected MixinRenderLivingBase(RenderManager renderManager) {
        super(renderManager);
    }

    @Inject(
            method = "prepareScale(Lnet/minecraft/entity/EntityLivingBase;F)F",
            require = 1,
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/GlStateManager;scale(FFF)V",
                    shift = Shift.AFTER))
    private void onPrepareScale(EntityLivingBase entity, float ticks, CallbackInfoReturnable<Float> ci) {
        LiteModBigPony bp = LiteLoader.getInstance().getMod(LiteModBigPony.class);
        bp.onRenderEntity(entity);
        this.shadowSize = bp.getUpdatedShadowSize(this.shadowSize, entity);
    }
}
