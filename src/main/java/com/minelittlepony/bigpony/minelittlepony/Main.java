package com.minelittlepony.bigpony.minelittlepony;

import com.minelittlepony.api.pony.Pony;
import com.minelittlepony.api.pony.meta.Size;
import com.minelittlepony.bigpony.*;
import com.minelittlepony.bigpony.client.BigPonyClient;
import com.minelittlepony.bigpony.data.BodyScale;
import com.minelittlepony.bigpony.data.CameraScale;
import com.minelittlepony.bigpony.data.EntityScale;
import com.minelittlepony.bigpony.hdskins.SkinDetecter;
import com.mojang.authlib.GameProfile;

import java.util.concurrent.CompletableFuture;

import com.minelittlepony.api.config.PonyConfig;
import com.minelittlepony.api.events.PonyDataCallback;
import com.minelittlepony.api.events.PonyModelPrepareCallback;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;

public class Main extends PresetDetector implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        INSTANCE = this;

        PonyModelPrepareCallback.EVENT.register((entity, model, mode) -> {
            if (BigPony.getInstance().getConfig().useDetectedPonyScaling.get() && isPony((PlayerEntity)entity)) {
                model.getAttributes().visualHeight = entity.getHeight() / model.getSize().scaleFactor();
            }
        });
        PonyDataCallback.EVENT.register((sender, data, env) -> {
            if (sender instanceof Scaling.Holder holder
                    && BigPony.getInstance().getConfig().useDetectedPonyScaling.get()
                    && env == EnvType.CLIENT && BigPonyClient.isClientPlayer(sender)) {
                detectPreset(sender.getGameProfile()).thenAccept(holder.getScaling()::setDimensions);
            }
        });
    }

    @Override
    public boolean isFillyCam() {
        return PonyConfig.getInstance().fillycam.get();
    }

    @Override
    public boolean isPony(PlayerEntity player) {
        return !Pony.getManager().getPony(player).race().isHuman();
    }

    @Override
    public CompletableFuture<EntityScale> detectPreset(GameProfile profile) {
        return SkinDetecter.getInstance().loadSkin(profile).thenApplyAsync(skin -> {
            // Turn on filly cam so we can get the camera parameters
            PonyConfig.getInstance().fillycam.set(true);

            Pony pony = Pony.getManager().getPony(skin);
            Size size = pony.metadata().size();

            EntityScale scale = new EntityScale(
                    BodyScale.of(size.scaleFactor()),
                    new CameraScale(size.eyeDistanceFactor(), size.eyeHeightFactor()),
                    false
            );

            // We turn off filly cam because it's not needed and might cause issues with buckets if left enabled
            PonyConfig.getInstance().fillycam.set(false);
            PonyConfig.getInstance().save();
            return scale;
        }, MinecraftClient.getInstance());
    }
}
