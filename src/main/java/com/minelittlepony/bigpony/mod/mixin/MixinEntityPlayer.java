package com.minelittlepony.bigpony.mod.mixin;

import com.minelittlepony.bigpony.mod.LiteModBigPony;
import com.mumfrey.liteloader.core.LiteLoader;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityPlayer.class)
public abstract class MixinEntityPlayer extends EntityLivingBase {

    public MixinEntityPlayer(World worldIn) {
        super(worldIn);
    }

    @ModifyConstant(method = "getEyeHeight()F", constant = @Constant(floatValue = 1.62F))
    private float modifyEyeHeight(float initial) {
        if ((EntityLivingBase) this instanceof EntityPlayerSP) // don't mess with other players
            return LiteLoader.getInstance().getMod(LiteModBigPony.class).getEyeHeight(initial);
        return initial;
    }

    // this is for forge
    @Redirect(method = "getEyeHeight()F", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/EntityPlayer;eyeHeight:F", remap = false))
    private float redirectEyeHeight(float initial) {
        if ((EntityLivingBase) this instanceof EntityPlayerSP) // don't mess with other players
            return LiteLoader.getInstance().getMod(LiteModBigPony.class).getEyeHeight(initial);
        return initial;
    }
}
