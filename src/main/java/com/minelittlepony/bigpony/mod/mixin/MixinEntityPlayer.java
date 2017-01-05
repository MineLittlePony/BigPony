package com.minelittlepony.bigpony.mod.mixin;

import com.minelittlepony.bigpony.mod.ducks.IEntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityPlayer.class)
@Implements(@Interface(iface = IEntityPlayer.class, prefix = "bigpony$"))
public abstract class MixinEntityPlayer extends EntityLivingBase {

    public float eyeHeight = 1.62F;

    public MixinEntityPlayer(World worldIn) {
        super(worldIn);
    }

    @ModifyConstant(method = "getEyeHeight()F", constant = @Constant(floatValue = 1.62F))
    private float modifyEyeHeight(float initial) {
        return eyeHeight;
    }

    // this is for forge
    @Redirect(method = "getEyeHeight()F", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/EntityPlayer;eyeHeight:F", remap = false))
    private float redirectEyeHeight(EntityPlayer initial) {
        return eyeHeight;
    }

    public void bigpony$setEyeHeight(float height) {
        this.eyeHeight = 1.62F * height;
    }

}
