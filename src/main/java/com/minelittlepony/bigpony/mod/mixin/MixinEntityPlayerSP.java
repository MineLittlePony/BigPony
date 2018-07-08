package com.minelittlepony.bigpony.mod.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.minelittlepony.bigpony.mod.ducks.IEntityPlayer;
import com.mojang.authlib.GameProfile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.stats.RecipeBook;
import net.minecraft.stats.StatisticsManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(EntityPlayerSP.class)
public abstract class MixinEntityPlayerSP extends AbstractClientPlayer {
    public MixinEntityPlayerSP(World worldIn, GameProfile playerProfile) {
        super(worldIn, playerProfile);
    }

    @Inject(method = "<init>(Lnet/minecraft/client/Minecraft;Lnet/minecraft/world/World;Lnet/minecraft/client/network/NetHandlerPlayClient;Lnet/minecraft/stats/StatisticsManager;Lnet/minecraft/stats/RecipeBook;)V",
            at = @At(value = "RETURN"))
    public void init(Minecraft mc, World w, NetHandlerPlayClient con, StatisticsManager stats, RecipeBook recipes, CallbackInfo cbi) {
        if (mc.player != null && mc.player.connection == con) {
            IEntityPlayer pl = (IEntityPlayer)mc.player;
            ((IEntityPlayer)this).setEyeHeight(pl.getHeightFactor());
        }
    }
	
	@Inject(method = "isOpenBlockSpace(Lnet/minecraft/util/math/BlockPos;)Z",
			at = @At("HEAD"),
			cancellable = true)
	private void injectIsOpenBlockSpace(BlockPos pos, CallbackInfoReturnable<Boolean> cbi) {
		int max = (int)Math.floor(this.height);
		
		while (pos.getY() <= max) {
			if (this.world.getBlockState(pos).isNormalCube()) {
				cbi.setReturnValue(false);
				return;
			}
			pos = pos.up();
		}
		cbi.setReturnValue(true);
		
		/*
		int max = (int)Math.floor(this.height);
		
		List<AxisAlignedBB> boxes = new ArrayList<AxisAlignedBB>();
		
		AxisAlignedBB aabb = this.getCollisionBoundingBox();
		
		while (pos.getY() <= max) {
			this.world.getBlockState(pos).addCollisionBoxToList(this.world, pos, aabb, boxes, this, false);
			
			if (!boxes.isEmpty()) {
				cbi.setReturnValue(false);
				return;
			}
			pos = pos.up();
		}
		cbi.setReturnValue(true);
		 */
    }
}
