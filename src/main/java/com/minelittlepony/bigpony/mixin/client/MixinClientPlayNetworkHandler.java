package com.minelittlepony.bigpony.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.minelittlepony.bigpony.Scaled;
import com.minelittlepony.bigpony.Scaling;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;

@Mixin(ClientPlayNetworkHandler.class)
abstract class MixinClientPlayNetworkHandler {
    private Scaling oldScaling;

    @Inject(method = "onPlayerRespawn", at = @At("HEAD"))
    private void beforePlayerRespawn(PlayerRespawnS2CPacket packet, CallbackInfo info) {
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player instanceof Scaled) {
            oldScaling = ((Scaled)player).getScaling();
        }
    }

    @Inject(method = "onPlayerRespawn", at = @At("RETURN"))
    private void afterPlayerRespawn(PlayerRespawnS2CPacket packet, CallbackInfo info) {
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (oldScaling != null && player instanceof Scaled) {
            ((Scaled)player).getScaling().copyFrom(oldScaling);
        }
    }
}
