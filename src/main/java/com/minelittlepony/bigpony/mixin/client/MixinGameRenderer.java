package com.minelittlepony.bigpony.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.minelittlepony.bigpony.client.BigPonyRenderState;
import com.minelittlepony.bigpony.data.BodyScale;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.util.Mth;

@Mixin(GameRenderer.class)
abstract class MixinGameRenderer {
    @Inject(method = "bobView(Lnet/minecraft/client/renderer/state/level/CameraRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;)V",
            at = @At("HEAD"),
            cancellable = true)
    private void onBobView(CameraRenderState cameraState, PoseStack poseStack, CallbackInfo info) {
        if (!cameraState.entityRenderState.isPlayer) {
            return;
        }

        if (cameraState instanceof BigPonyRenderState.Holder holder) {
            info.cancel();

            float backwardsInterpolatedWalkDistance = cameraState.entityRenderState.backwardsInterpolatedWalkDistance;
            float bob = cameraState.entityRenderState.bob;

            BodyScale scale = holder.getBigPonyState().bodyScale;

            poseStack.translate(
                    (Mth.sin(backwardsInterpolatedWalkDistance * Mth.PI) * bob * 0.5F) * scale.x(),
                    -Math.abs(Mth.cos(backwardsInterpolatedWalkDistance * Mth.PI) * bob) * scale.y(),
                    0
            );
            poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.sin(backwardsInterpolatedWalkDistance * Mth.PI) * bob * 3 * scale.z()));
            poseStack.mulPose(Axis.XP.rotationDegrees(Mth.abs(Mth.cos(backwardsInterpolatedWalkDistance * Mth.PI - 0.2F) * bob) * 5 * scale.x()));
        }
    }
}
