package com.minelittlepony.bigpony.mod;

import com.minelittlepony.bigpony.mod.ducks.IEntityPlayer;
import com.mumfrey.liteloader.core.LiteLoader;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import playersync.client.api.PlayerSync;
import playersync.client.api.SyncManager;

import java.util.UUID;
import javax.annotation.Nullable;

public class PlayerSizeManager {
    @Nullable
    private final SyncManager manager;

    private static final String PSYNC = "playersync",
                                CHANNEL = "bigpony|scale";

    public PlayerSizeManager(LiteLoader loader, IPlayerScale initial) {
      if (loader.isModActive(PSYNC)) {
          manager = PlayerSync.getManager();
          manager.register(CHANNEL, new PlayerScale.Serializer(), (chan, uuid, obj) -> handlePacket(uuid, obj), initial);
          LiteModBigPony.logger.info("PlayerSync detected!");
      } else {
          manager = null;
          LiteModBigPony.logger.warn("PlayerSync not detected! Client synchronization will be disabled.");
      }
    }

    private void handlePacket(UUID uuid, IPlayerScale size) {
        IEntityPlayer player = getPlayer(uuid);
        if (player == null) {
            LiteModBigPony.logger.warn("Received scale data for unknown player id " + uuid);
            return;
        }

        player.setPlayerScale(size);

        LiteModBigPony.logger.trace("Received scale data for player " + ((EntityPlayer)player).getGameProfile());
    }

    @Nullable
    private IEntityPlayer getPlayer(UUID uuid) {
        return (IEntityPlayer)Minecraft.getMinecraft().world.getPlayerEntityByUUID(uuid);
    }
    
    public float getShadowScale(IEntityPlayer player) {
        IPlayerScale scale = player.getPlayerScale();
        return Math.max(scale.getXScale(), scale.getZScale());
    }

    public void setScale(IEntityPlayer ep, IPlayerScale scale) {
        ep.setPlayerScale(scale);

        if (manager != null) {
          manager.sendPacket(CHANNEL, scale);
        } else {
          Minecraft mc = Minecraft.getMinecraft();

          if (mc.isIntegratedServerRunning()) {
            ep = (IEntityPlayer)mc.getIntegratedServer().getPlayerList().getPlayerByUUID(((EntityPlayer)ep).getUniqueID());
            ep.setPlayerScale(scale);
          }
        }
    }
}
