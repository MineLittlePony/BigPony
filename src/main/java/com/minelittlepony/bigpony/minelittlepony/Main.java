package com.minelittlepony.bigpony.minelittlepony;

import com.minelittlepony.api.pony.IPony;
import com.minelittlepony.api.pony.meta.Size;
import com.minelittlepony.bigpony.Cam;
import com.minelittlepony.bigpony.Scaled;
import com.minelittlepony.bigpony.Scaling;
import com.minelittlepony.bigpony.Triple;
import com.minelittlepony.bigpony.hdskins.SkinDetecter;
import com.minelittlepony.client.MineLittlePony;
import com.mojang.authlib.GameProfile;

import java.util.concurrent.CompletableFuture;

import com.minelittlepony.api.model.IModel;
import com.minelittlepony.api.model.ModelAttributes;
import com.minelittlepony.api.model.fabric.PonyModelPrepareCallback;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public class Main extends PresetDetector implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        INSTANCE = this;

        PonyModelPrepareCallback.EVENT.register(this::onPonyModelPrepared);
    }

    private void onPonyModelPrepared(Entity entity, IModel model, ModelAttributes.Mode mode) {

        if (entity instanceof Scaled && !((Scaled)entity).getScaling().isVisual() && isPony((PlayerEntity)entity)) {
            model.getAttributes().visualHeight = entity.getHeight() / model.getSize().getScaleFactor();
        }
    }

    @Override
    public boolean isFillyCam() {
        return MineLittlePony.getInstance().getConfig().fillycam.get();
    }

    @Override
    public boolean isPony(PlayerEntity player) {
        return !MineLittlePony.getInstance().getManager().getPony(player).getRace(false).isHuman();
    }

    @Override
    public CompletableFuture<Identifier> detectPreset(GameProfile profile, Scaling into) {
        return SkinDetecter.getInstance().loadSkin(profile).thenApplyAsync(skin -> {
            // Turn on filly cam so we can get the camera parameters
            MineLittlePony.getInstance().getConfig().fillycam.set(true);

            IPony pony = IPony.forResource(skin);
            Size size = pony.getMetadata().getSize();

            into.setScale(new Triple(size.getScaleFactor()));
            into.setCamera(new Cam(size.getEyeDistanceFactor(), size.getEyeHeightFactor()));

            // We turn off filly cam because it's not needed and might cause issues with buckets if left enabled
            MineLittlePony.getInstance().getConfig().fillycam.set(false);
            MineLittlePony.getInstance().getConfig().save();
            return skin;
        }, MinecraftClient.getInstance());
    }
}
