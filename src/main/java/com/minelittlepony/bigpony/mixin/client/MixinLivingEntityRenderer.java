package com.minelittlepony.bigpony.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import com.minelittlepony.bigpony.Scaling;
import com.minelittlepony.bigpony.data.BodyScale;

import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;

@Mixin(LivingEntityRenderer.class)
abstract class MixinLivingEntityRenderer<T extends LivingEntity, M extends EntityModel<T>> {


    @ModifyVariable(method = "Lnet/minecraft/client/render/entity/LivingEntityRenderer;setupTransforms("
                        + "Lnet/minecraft/entity/LivingEntity;"
                        + "Lnet/minecraft/client/util/math/MatrixStack;"
                        + "FFF"
                        + ")V",
            at = @At("HEAD"),
            ordinal = 3
    )
    private float modifyScaleonSetupTransforms(float initial, T entity, MatrixStack matrices) {
        if (entity instanceof Scaling.Holder holder) {
            BodyScale scale = holder.getScaling().getRenderedBodyScale();
            matrices.scale(scale.x(), scale.y(), scale.z());
            return initial * scale.y();
        }

        return initial;
    }
}
