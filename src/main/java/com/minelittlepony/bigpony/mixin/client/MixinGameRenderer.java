package com.minelittlepony.bigpony.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.minelittlepony.bigpony.Scaled;
import com.minelittlepony.bigpony.Triple;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.resource.SynchronousResourceReloader;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;

@Mixin(GameRenderer.class)
abstract class MixinGameRenderer implements SynchronousResourceReloader, AutoCloseable {
    @Inject(method = "bobView(Lnet/minecraft/client/util/math/MatrixStack;F)V",
            at = @At("HEAD"),
            cancellable = true)
    private void onBobView(MatrixStack matrices, float f, CallbackInfo info) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (!(client.getCameraEntity() instanceof PlayerEntity)) {
            return;
        }

        info.cancel();

        PlayerEntity player = (PlayerEntity)client.getCameraEntity();
        Triple scale = ((Scaled)player).getScaling().getVisualScale();

        float g = player.horizontalSpeed - player.prevHorizontalSpeed;
        float h = -(player.horizontalSpeed + g * f);
        float i = MathHelper.lerp(f, player.prevStrideDistance, player.strideDistance);

        matrices.translate(
                (MathHelper.sin(h * (float)Math.PI) * i / 2) * scale.x,
                -Math.abs(MathHelper.cos(h * (float)Math.PI) * i) * scale.y,
                0
        );
        matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(
                MathHelper.sin(h * (float)Math.PI) * i * 3 * scale.z
        ));
        matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(
                Math.abs(MathHelper.cos(h * (float)Math.PI - 0.2f) * i) * 5 * scale.x
        ));
    }
}
