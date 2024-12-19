package com.minelittlepony.bigpony.minelittlepony;

import com.minelittlepony.api.pony.IPony;
import com.minelittlepony.api.pony.meta.Size;
import com.minelittlepony.api.pony.network.fabric.PonyDataCallback;
import com.minelittlepony.bigpony.*;
import com.minelittlepony.bigpony.client.BigPonyClient;
import com.minelittlepony.bigpony.data.BodyScale;
import com.minelittlepony.bigpony.data.CameraScale;
import com.minelittlepony.bigpony.data.EntityScale;
import com.minelittlepony.bigpony.hdskins.SkinDetecter;
import com.mojang.authlib.GameProfile;

import java.util.concurrent.CompletableFuture;

import com.minelittlepony.api.config.PonyConfig;
import com.minelittlepony.api.model.fabric.PonyModelPrepareCallback;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;

public class Main extends PresetDetector implements ClientModInitializer {

    private boolean oldFillyCam;
    private boolean writing;

    @Override
    public void onInitializeClient() {
        INSTANCE = this;

        PonyModelPrepareCallback.EVENT.register((entity, model, mode) -> {
            if (BigPony.getInstance().getConfig().useDetectedPonyScaling.get()
                    && entity instanceof PlayerEntity player
                    && BigPonyClient.isClientPlayer(player)
                    && isPony(player)) {
                model.getAttributes().visualHeight = entity.getHeight();
            }
        });
        PonyDataCallback.EVENT.register((sender, data, noSkin, env) -> {
            if (sender instanceof Scaling.Holder holder
                    && BigPony.getInstance().getConfig().useDetectedPonyScaling.get()
                    && env == EnvType.CLIENT && BigPonyClient.isClientPlayer(sender)) {
                detectPreset(sender.getGameProfile()).thenAccept(holder.getScaling()::setDimensions);
            }
        });

        // TODO: Not implements
        /*
        PonyConfig.getInstance().onChangedExternally(config -> {
            if (!writing) {
                oldFillyCam = isFillyCam();
            }
        });*/
        PonyConfig.getInstance().fillycam.onChanged(fillyCam -> {
            if (!writing) {
                oldFillyCam = isFillyCam();
            }
        });
        oldFillyCam = isFillyCam();
    }

    public void setFillyCam(boolean enable) {
        oldFillyCam = isFillyCam();
        writing = true;
        PonyConfig.getInstance().fillycam.set(enable);
        writing = false;
    }

    @Override
    public void revertFillyCam() {
        PonyConfig.getInstance().fillycam.set(oldFillyCam);
    }

    @Override
    public boolean isFillyCam() {
        return PonyConfig.getInstance().fillycam.get();
    }

    @Override
    public boolean isPony(PlayerEntity player) {
        return !IPony.getManager().getPony(player).race().isHuman();
    }

    @Override
    public CompletableFuture<EntityScale> detectPreset(GameProfile profile) {
        return SkinDetecter.getInstance().loadSkin(profile).thenApplyAsync(skin -> {
            // Turn on filly cam so we can get the camera parameters
            boolean fillyCam = isFillyCam();
            setFillyCam(true);

            IPony pony = IPony.getManager().getPony(skin);
            Size size = pony.metadata().getSize();

            EntityScale scale = new EntityScale(
                    BodyScale.of(size.getScaleFactor()),
                    new CameraScale(size.getEyeDistanceFactor(), size.getEyeHeightFactor()),
                    false
            );

            // We turn off filly cam because it's not needed and might cause issues with buckets if left enabled
            setFillyCam(false);
            if (!fillyCam) {
                PonyConfig.getInstance().save();
            }
            return scale;
        }, MinecraftClient.getInstance());
    }
}
