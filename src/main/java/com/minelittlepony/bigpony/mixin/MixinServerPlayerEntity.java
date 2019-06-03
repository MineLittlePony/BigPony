package com.minelittlepony.bigpony.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.minelittlepony.bigpony.ducks.IEntityPlayer;

import net.minecraft.container.ContainerListener;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;

@Mixin(ServerPlayerEntity.class)
public abstract class MixinServerPlayerEntity extends PlayerEntity implements ContainerListener {

    MixinServerPlayerEntity() { super(null, null); }

    @Inject(method = "copyFrom(Lnet/minecraft/server/network/ServerPlayerEntity;Z)V", at = @At("RETURN"))
    public void injectCopyFrom(ServerPlayerEntity other, boolean keepEverything, CallbackInfo info) {
        ((IEntityPlayer)this).setPlayerScale(((IEntityPlayer)other).getPlayerScale());
    }
}
