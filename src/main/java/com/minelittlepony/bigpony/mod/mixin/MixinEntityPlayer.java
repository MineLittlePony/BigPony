package com.minelittlepony.bigpony.mod.mixin;

import com.minelittlepony.bigpony.mod.LiteModBigPony;
import com.mumfrey.liteloader.core.LiteLoader;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(EntityPlayer.class)
public abstract class MixinEntityPlayer extends EntityLivingBase {

    public MixinEntityPlayer(World worldIn) {
        super(worldIn);
    }

    @ModifyConstant(method = "getEyeHeight()F", constant = @Constant(floatValue = 1.62F), require = 1)
    private float onGetEyeHeight(float initial) {
        if ((EntityLivingBase) this instanceof EntityPlayerSP) // don't mess with other players
            return LiteLoader.getInstance().getMod(LiteModBigPony.class).getEyeHeight(initial);
        return initial;
    }

}
