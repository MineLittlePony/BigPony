package com.minelittlepony.bigpony.mod.mixin;

import com.minelittlepony.bigpony.mod.ducks.IEntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityPlayer.class)
@Implements(@Interface(iface = IEntityPlayer.class, prefix = "bigpony$"))
public abstract class MixinEntityPlayer extends EntityLivingBase {

    public float heightFactor = 1F;
    public float eyeHeight = 1.62F;
    
    @Shadow
    protected abstract void updateSize();
    
    public MixinEntityPlayer(World worldIn) {
        super(worldIn);
    }

    @ModifyConstant(method = "getEyeHeight()F", constant = @Constant(floatValue = 1.62F))
    private float modifyEyeHeight(float initial) {
        return eyeHeight;
    }

    // this is for forge
    @Redirect(method = "getEyeHeight()F",
              at = @At(value = "FIELD",
                       target = "Lnet/minecraft/entity/player/EntityPlayer;eyeHeight:F",
                       remap = false))
    private float redirectEyeHeight(EntityPlayer initial) {
        return eyeHeight;
    }

    @Inject(method = "getEyeHeight()F",
            at = @At("RETURN"),
            cancellable = true)
    private void fixNegativeHeights(CallbackInfoReturnable<Float> cir) {
        // prevent you from seeing under the ground when looking down.
        if (cir.getReturnValueF() < 0.15F) {
            cir.setReturnValue(0.15F);
        }
    }

    public void bigpony$setEyeHeight(float height) {
        heightFactor = height;
        eyeHeight = 1.62F * height;
        updateSize();
    }

    public float bigpony$getHeightFactor() {
        return this.heightFactor;
    }
    
    @ModifyConstant(method = "updateSize()V", constant = @Constant(floatValue = 1.8F))
    private float modifyStandingHeight(float initial) {
        return initial * this.heightFactor;
    }
    
    @ModifyConstant(method = "updateSize()V", constant = @Constant(floatValue = 1.65F))
    private float modifySneakingHeight(float initial) {
        return initial * this.heightFactor;
    }
    
    @ModifyConstant(method = "updateSize()V", constant = @Constant(floatValue = 0.6F))
    private float modifyWidth(float initial) {
        return Math.max(initial * this.heightFactor, 0.3F);
    }
}
