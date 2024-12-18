package com.minelittlepony.bigpony.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.minelittlepony.bigpony.Scaling;
import com.minelittlepony.bigpony.data.EntityScale;
import com.mojang.datafixers.util.Pair;

import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;

@Mixin(value = PlayerEntity.class, priority = 1001)
abstract class MixinPlayerEntity extends LivingEntity implements Scaling.Holder {
    private MixinPlayerEntity() {super(null, null);}

    private final Scaling playerScale = new Scaling();

    @ModifyReturnValue(method = "getBaseDimensions(Lnet/minecraft/entity/EntityPose;)Lnet/minecraft/entity/EntityDimensions;", at = @At("RETURN"))
    private EntityDimensions modifyEntityDimensions(EntityDimensions dimensions, EntityPose pose) {
        return getScaling().getReplacementSize((PlayerEntity)(Object)this, pose, dimensions);
    }

    @Inject(method = "writeCustomDataToNbt(Lnet/minecraft/nbt/NbtCompound;)V", at = @At("HEAD"))
    private void onWriteCustomDataToTag(NbtCompound tag, CallbackInfo info) {
        EntityScale.CODEC.encodeStart(NbtOps.INSTANCE, getScaling().getDimensions()).result().ifPresent(nbt -> {
            tag.put("big_pony_data", nbt);
        });
    }

    @Inject(method = "readCustomDataFromNbt(Lnet/minecraft/nbt/NbtCompound;)V", at = @At("HEAD"))
    private void onReadCustomDataFromTag(NbtCompound tag, CallbackInfo info) {
        if (tag.contains("big_pony_data", NbtElement.COMPOUND_TYPE)) {
            EntityScale.CODEC.decode(NbtOps.INSTANCE, tag.getCompound("big_pony_data")).result().map(Pair::getFirst).ifPresent(getScaling()::setDimensions);
        }
    }

    @Inject(method = "tick()V", at = @At("RETURN"))
    private void afterTick(CallbackInfo info) {
        getScaling().tick((PlayerEntity)(Object)this);
    }

    @Override
    public Scaling getScaling() {
        return playerScale;
    }
}
