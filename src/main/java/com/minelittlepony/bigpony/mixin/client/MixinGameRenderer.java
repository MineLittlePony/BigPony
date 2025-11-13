package com.minelittlepony.bigpony.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.minelittlepony.bigpony.client.BigPonyRenderState;
import com.minelittlepony.bigpony.data.BodyScale;
import com.minelittlepony.bigpony.network.InteractionManager;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.SynchronousResourceReloader;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

@Mixin(GameRenderer.class)
abstract class MixinGameRenderer implements SynchronousResourceReloader, AutoCloseable {
    @Inject(method = "bobView(Lnet/minecraft/client/util/math/MatrixStack;F)V",
            at = @At("HEAD"),
            cancellable = true)
    private void onBobView(MatrixStack matrices, float tickDelta, CallbackInfo info) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (!(client.getCameraEntity() instanceof AbstractClientPlayerEntity player)) {
            return;
        }

        if (client.getEntityRenderDispatcher().getRenderer(player).getAndUpdateRenderState(player, tickDelta) instanceof BigPonyRenderState.Holder holder) {
            info.cancel();

            float h = player.getState().getReverseLerpedDistanceMoved(tickDelta);
            float i = player.getState().lerpMovement(tickDelta);

            BodyScale scale = holder.getBigPonyState().bodyScale;
            float xScale = InteractionManager.getInstance().getClamped(scale.x());
            float yScale = InteractionManager.getInstance().getClamped(scale.y());
            float zScale = InteractionManager.getInstance().getClamped(scale.z());

            matrices.translate(
                    (MathHelper.sin(h * MathHelper.PI) * i * 0.5F) * xScale,
                    -Math.abs(MathHelper.cos(h * MathHelper.PI) * i) * yScale,
                    0
            );
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(
                    MathHelper.sin(h * MathHelper.PI) * i * 3 * zScale
            ));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(
                    Math.abs(MathHelper.cos(h * MathHelper.PI - 0.2F) * i) * 5 * xScale
            ));
        }
    }
}
