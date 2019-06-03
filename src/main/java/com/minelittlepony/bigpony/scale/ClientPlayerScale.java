package com.minelittlepony.bigpony.scale;

import com.minelittlepony.bigpony.BigPony;
import com.minelittlepony.bigpony.JsonConfig;
import com.minelittlepony.bigpony.ducks.IEntityPlayer;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;

public class ClientPlayerScale extends PlayerScale {

    public ClientPlayerScale() {
        super(1, 1, 1, 1);
    }

    public void setScale(IEntityPlayer ep, IPlayerScale scale) {
        ep.setPlayerScale(scale);

        MinecraftClient mc = MinecraftClient.getInstance();

        if (mc.isIntegratedServerRunning()) {
            ep = (IEntityPlayer) mc.getServer().getPlayerManager().getPlayer(((PlayerEntity) ep).getUuid());
            ep.setPlayerScale(scale);
        }
    }

    @Override
    protected void markDirty() {
        super.markDirty();

        JsonConfig config = BigPony.getInstance().getConfig();
        MinecraftClient mc = MinecraftClient.getInstance();

        setScale((IEntityPlayer) mc.player, config.data);

        config.save();
    }
}
