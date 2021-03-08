package com.minelittlepony.bigpony;

import java.nio.file.Path;

import com.minelittlepony.common.util.settings.JsonConfig;
import com.minelittlepony.common.util.settings.Setting;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;

public class Config extends JsonConfig {

    public final Setting<ClientPlayerScale> scale = value("scale", new ClientPlayerScale());

    public Config(Path path) {
        super(path);
    }

    static class ClientPlayerScale extends Scaling {

        public ClientPlayerScale() {
            super(new Triple(1), 1, 1);
        }

        @Override
        public void markDirty() {
            super.markDirty();

            Scaled ep = (Scaled) MinecraftClient.getInstance().player;

            ep.setScale(this);

            MinecraftClient mc = MinecraftClient.getInstance();

            if (mc.isIntegratedServerRunning()) {
                ep = (Scaled) mc.getServer().getPlayerManager().getPlayer(((PlayerEntity) ep).getUuid());
                ep.setScale(this);
            }

            BigPony.getInstance().getConfig().save();
        }
    }
}
