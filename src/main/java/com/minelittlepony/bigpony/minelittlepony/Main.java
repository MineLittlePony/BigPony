package com.minelittlepony.bigpony.minelittlepony;

import com.minelittlepony.api.pony.Pony;
import com.minelittlepony.api.pony.meta.Size;
import com.minelittlepony.bigpony.*;
import com.minelittlepony.bigpony.client.BigPonyClient;
import com.minelittlepony.bigpony.hdskins.SkinDetecter;
import com.mojang.authlib.GameProfile;

import java.util.concurrent.CompletableFuture;

import com.minelittlepony.api.model.PonyModel;
import com.minelittlepony.api.config.PonyConfig;
import com.minelittlepony.api.events.PonyDataCallback;
import com.minelittlepony.api.events.PonyModelPrepareCallback;
import com.minelittlepony.api.model.ModelAttributes;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public class Main extends PresetDetector implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        INSTANCE = this;

        PonyModelPrepareCallback.EVENT.register(this::onPonyModelPrepared);
        PonyDataCallback.EVENT.register((sender, data, env) -> {
            if (!BigPony.getInstance().getScaling().isVisual()
                    && env == EnvType.CLIENT
                    && BigPonyClient.isClientPlayer(sender)) {
                detectPreset(sender.getGameProfile(), ((Scaled)sender).getScaling());
            }
        });
    }

    private void onPonyModelPrepared(Entity entity, PonyModel<?> model, ModelAttributes.Mode mode) {
        if (entity instanceof Scaled && !((Scaled)entity).getScaling().isVisual() && isPony((PlayerEntity)entity)) {
            model.getAttributes().visualHeight = entity.getHeight() / model.getSize().scaleFactor();
        }
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
    public CompletableFuture<Identifier> detectPreset(GameProfile profile, Scaling into) {
        return SkinDetecter.getInstance().loadSkin(profile).thenApplyAsync(skin -> {
            // Turn on filly cam so we can get the camera parameters
            PonyConfig.getInstance().fillycam.set(true);

            Pony pony = Pony.getManager().getPony(skin);
            Size size = pony.metadata().size();

            into.setScale(new Triple(size.scaleFactor()));
            into.setCamera(new Cam(size.eyeDistanceFactor(), size.eyeHeightFactor()));

            // We turn off filly cam because it's not needed and might cause issues with buckets if left enabled
            PonyConfig.getInstance().fillycam.set(false);
            PonyConfig.getInstance().save();
            return skin;
        }, MinecraftClient.getInstance());
    }
}
