package com.minelittlepony.bigpony.minelittlepony;

import com.minelittlepony.api.pony.IPony;
import com.minelittlepony.api.pony.IPonyData;
import com.minelittlepony.api.pony.meta.Size;
import com.minelittlepony.api.pony.network.fabric.PonyDataCallback;
import com.minelittlepony.bigpony.*;
import com.minelittlepony.bigpony.client.BigPonyClient;
import com.minelittlepony.bigpony.hdskins.SkinDetecter;
import com.mojang.authlib.GameProfile;

import java.util.concurrent.CompletableFuture;

import com.minelittlepony.api.config.PonyConfig;
import com.minelittlepony.api.model.fabric.PonyModelPrepareCallback;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public class Main extends PresetDetector implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        INSTANCE = this;

        PonyModelPrepareCallback.EVENT.register((entity, model, mode) -> {
            if (entity instanceof Scaled && !((Scaled)entity).getScaling().isVisual() && isPony((PlayerEntity)entity)) {
                model.getAttributes().visualHeight = entity.getHeight() / model.getSize().getScaleFactor();
            }
        });
        PonyDataCallback.EVENT.register((sender, data, noSkin, env) -> {
            if (!BigPony.getInstance().getScaling().isVisual()
                    && env == EnvType.CLIENT
                    && BigPonyClient.isClientPlayer(sender)) {
                applyPreset(IPony.getManager().getPony(sender).metadata(), ((Scaled)sender).getScaling());
            }
        });
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
    public CompletableFuture<Identifier> detectPreset(GameProfile profile, Scaling into) {
        return SkinDetecter.getInstance().loadSkin(profile).thenApplyAsync(skin -> {
            applyPreset(IPony.getManager().getPony(skin).metadata(), into);
            return skin;
        }, MinecraftClient.getInstance());
    }

    private void applyPreset(IPonyData data, Scaling into) {
        Size size = data.getSize();
        boolean fillyCam = PonyConfig.getInstance().fillycam.get();
        PonyConfig.getInstance().fillycam.set(true);
        into.setScale(new Triple(size.getScaleFactor()));
        into.setCamera(new Cam(size.getEyeDistanceFactor(), size.getEyeHeightFactor()));
        PonyConfig.getInstance().fillycam.set(fillyCam);
    }
}
