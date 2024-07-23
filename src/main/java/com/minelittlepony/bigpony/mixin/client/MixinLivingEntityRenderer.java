package com.minelittlepony.bigpony.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.minelittlepony.bigpony.Scaling;
import com.minelittlepony.bigpony.data.BodyScale;
import com.minelittlepony.bigpony.network.InteractionManager;

import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;

@Mixin(LivingEntityRenderer.class)
abstract class MixinLivingEntityRenderer<T extends LivingEntity, M extends EntityModel<T>> {
    @ModifyVariable(
        method = "setupTransforms",
        at = @At("HEAD"),
        ordinal = 3,
        argsOnly = true
    )
    private float modifyScaleOnSetupTransforms(float initial, T entity, MatrixStack matrices, float animationProgress, float bodyYaw, float tickDelta, float vanillaScale) {
        if (entity instanceof Scaling.Holder holder) {
            BodyScale scale = holder.getScaling().getRenderedBodyScale();
            matrices.scale(
                    InteractionManager.getInstance().getClamped(scale.x()),
                    InteractionManager.getInstance().getClamped(scale.y()),
                    InteractionManager.getInstance().getClamped(scale.z())
            );
            return initial * InteractionManager.getInstance().getClamped(scale.y());
        }

        return initial;
    }
}
