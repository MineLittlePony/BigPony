package com.minelittlepony.bigpony.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.minelittlepony.bigpony.BigPony;
import com.minelittlepony.bigpony.ducks.IEntityPlayer;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.recipe.book.ClientRecipeBook;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.stat.StatHandler;

@Mixin(ClientPlayerEntity.class)
public abstract class MixinClientPlayerEntity extends AbstractClientPlayerEntity {
    MixinClientPlayerEntity() { super(null, null); }

    @Inject(method = "<init>("
            + "Lnet/minecraft/client/MinecraftClient;"
            + "Lnet/minecraft/client/world/ClientWorld;"
            + "Lnet/minecraft/client/network/ClientPlayNetworkHandler;"
            + "Lnet/minecraft/stats/StatHandler;"
            + "Lnet/minecraft/client/recipe/book/ClientRecipeBook;)V",
            at = @At(value = "RETURN"))
    public void init(MinecraftClient mc, ClientWorld w, ClientPlayNetworkHandler con, StatHandler stats, ClientRecipeBook recipes, CallbackInfo cbi) {
        if (mc.player != null && mc.player.networkHandler == con) {
            ((IEntityPlayer)this).setPlayerScale(BigPony.getInstance().getConfig().data);
        }
    }
}
