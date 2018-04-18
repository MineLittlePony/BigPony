package com.minelittlepony.bigpony.mod.mixin;

import com.minelittlepony.bigpony.mod.IPlayerScale;
import com.minelittlepony.bigpony.mod.PlayerScale;
import com.minelittlepony.bigpony.mod.ducks.IEntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityPlayer.class)
public abstract class MixinEntityPlayer extends EntityLivingBase implements IEntityPlayer {

    private IPlayerScale playerScale = new PlayerScale(1, 1, 1, 1);

    public MixinEntityPlayer(World worldIn) {
        super(worldIn);
    }

    @ModifyConstant(method = "getEyeHeight()F", constant = @Constant(floatValue = 1.62F))
    private float modifyEyeHeight(float initial) {
        return initial * playerScale.getHeight();
    }

    // this is for forge
    public float eyeHeight = 1.62F;

    @Redirect(method = "getEyeHeight()F", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/EntityPlayer;eyeHeight:F", remap = false))
    private float redirectEyeHeight(EntityPlayer initial) {
        return eyeHeight;
    }
    //

    @Inject(method = "getEyeHeight()F",
            at = @At("RETURN"),
            cancellable = true)
    private void fixNegativeHeights(CallbackInfoReturnable<Float> cir) {
        // prevent you from seeing under the ground when looking down.
        if (cir.getReturnValueF() < 0.15F) {
            cir.setReturnValue(0.15F);
        }
    }

    @Override
    public IPlayerScale getPlayerScale() {
        return playerScale;
    }

    @Override
    public void setPlayerScale(IPlayerScale scale) {
        eyeHeight = 1.62F * scale.getHeight();
        playerScale.copyFrom(scale);
    }

    @Override
    public void updatePlayerScale(IEntityPlayer old) {
        playerScale = old.getPlayerScale();
    }
}
