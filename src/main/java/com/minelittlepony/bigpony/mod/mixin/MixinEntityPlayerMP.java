package com.minelittlepony.bigpony.mod.mixin;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.minelittlepony.bigpony.mod.ducks.IEntityPlayer;
import com.mojang.authlib.GameProfile;

@Mixin(EntityPlayerMP.class)
public abstract class MixinEntityPlayerMP extends EntityPlayer implements IContainerListener {
	
    public MixinEntityPlayerMP(World worldIn, GameProfile gm) {
		super(worldIn, gm);
	}

	@Inject(method = "copyFrom(Lnet/minecraft/entity/EntityPlayerMP;Z)V", at = @At("RETURN"))
    public void injectCopyFrom(EntityPlayerMP other, boolean keepEverything, CallbackInfo cbi) {
    	((IEntityPlayer)this).setEyeHeight(((IEntityPlayer)other).getHeightFactor());
    }
}
