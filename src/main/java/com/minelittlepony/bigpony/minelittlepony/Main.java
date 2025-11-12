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
import com.minelittlepony.api.events.PonyRenderStatePrepareCallback;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;

public class Main extends PresetDetector implements ClientModInitializer {

    private boolean oldFillyCam;
    private boolean writing;

    @Override
    public void onInitializeClient() {
        INSTANCE = this;

        PonyRenderStatePrepareCallback.EVENT.register((state, model, mode) -> {
            if (BigPony.getInstance().getConfig().useDetectedPonyScaling.get()
                    && state.getAttributes().isPlayer
                    && BigPonyClient.isClientPlayer(state.getAttributes().getEntityId())
                    && !PonyConfig.getEffectiveRace(state.getAttributes().metadata.race()).isHuman()) {
                state.getAttributes().visualHeight = MinecraftClient.getInstance().player.getHeight();
            }
        });
        PonyDataCallback.EVENT.register((sender, data, env) -> {
            if (sender instanceof Scaling.Holder holder
                    && sender instanceof ClientPlayerEntity player
                    && BigPony.getInstance().getConfig().useDetectedPonyScaling.get()
                    && env == EnvType.CLIENT && BigPonyClient.isClientPlayer(player)) {
                detectPreset(player.getGameProfile()).thenAccept(holder.getScaling()::setDimensions);
            }
        });

        PonyConfig.getInstance().onChangedExternally(config -> {
            if (!writing) {
                oldFillyCam = isFillyCam();
            }
        });
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
    public boolean isPony(LivingEntity entity) {
        return Pony.getManager().getPony(entity).filter(pony -> !pony.race().isHuman()).isPresent();
    }

    @Override
    public CompletableFuture<EntityScale> detectPreset(GameProfile profile) {
        return SkinDetecter.getInstance().loadSkin(profile).thenApplyAsync(skin -> {
            // Turn on filly cam so we can get the camera parameters
            boolean fillyCam = isFillyCam();
            setFillyCam(true);

            Size size = Pony.getManager().getPony(skin).size();

            EntityScale scale = new EntityScale(
                    BodyScale.of(size.scaleFactor()),
                    new CameraScale(size.eyeDistanceFactor(), size.eyeHeightFactor()),
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
