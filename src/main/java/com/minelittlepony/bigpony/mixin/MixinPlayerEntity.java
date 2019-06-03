package com.minelittlepony.bigpony.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.minelittlepony.bigpony.ducks.IEntityPlayer;
import com.minelittlepony.bigpony.scale.IPlayerScale;
import com.minelittlepony.bigpony.scale.PlayerScale;

import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

@Mixin(PlayerEntity.class)
public abstract class MixinPlayerEntity extends LivingEntity implements IEntityPlayer {

    private MixinPlayerEntity() {super(null, null);}

    private IPlayerScale playerScale;

    @Inject(method = "getSize(Lnet/minecraft/entity/EntityPose;)Lnet/minecraft/entity/EntitySize;",
            at = @At("RETURN"),
            cancellable = true)
    protected void redirectGetSize(EntityPose pose, CallbackInfoReturnable<EntitySize> info) {
        info.setReturnValue(getPlayerScale().getReplacementSize(pose, info.getReturnValue()));
    }

    @Inject(method = "getActiveEyeHeight(Lnet/minecraft/entity/EntityPose;Lnet/minecraft/entity/EntitySize;)F",
            at = @At("RETURN"),
            cancellable = true)
    protected void redirectGetActiveEyeHeight(EntityPose pose, EntitySize size, CallbackInfoReturnable<Float> info) {
        info.setReturnValue(getPlayerScale().getReplacementActiveEyeHeight(pose, size, info.getReturnValue()));
    }

    @Override
    public IPlayerScale getPlayerScale() {
        if (playerScale == null) {
            playerScale = new PlayerScale(1, 1, 1, 1);
        }
        return playerScale;
    }

    @Override
    public void setPlayerScale(IPlayerScale scale) {
        playerScale = scale;
        refreshSize();
    }
}
