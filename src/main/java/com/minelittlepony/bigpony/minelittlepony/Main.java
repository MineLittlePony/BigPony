package com.minelittlepony.bigpony.minelittlepony;

import com.minelittlepony.api.pony.Pony;
import com.minelittlepony.api.pony.meta.Size;
import com.minelittlepony.bigpony.*;
import com.minelittlepony.bigpony.client.BigPonyClient;
import com.minelittlepony.bigpony.client.gui.GuiBigSettings;
import com.minelittlepony.bigpony.data.BodyScale;
import com.minelittlepony.bigpony.data.CameraScale;
import com.minelittlepony.bigpony.data.EntityScale;
import com.minelittlepony.bigpony.hdskins.SkinDetecter;
import com.minelittlepony.bigpony.network.InteractionManager;
import com.minelittlepony.common.client.gui.GameGui;
import com.mojang.authlib.GameProfile;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import com.minelittlepony.api.config.PonyConfig;
import com.minelittlepony.api.events.PonyDataCallback;
import com.minelittlepony.api.events.PonyRenderStatePrepareCallback;
import com.minelittlepony.api.model.PonyModel;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;

public class Main extends PresetDetector implements ClientModInitializer {

    private Boolean oldFillycam;
    private boolean setFillycam;

    private boolean switching;

    @Override
    public void onInitializeClient() {
        INSTANCE = this;

        PonyRenderStatePrepareCallback.EVENT.register((state, _, _) -> {
            if (BigPony.getInstance().getConfig().useDetectedPonyScaling.get()
                    && state.getAttributes().isPlayer
                    && BigPonyClient.isClientPlayer(state.getAttributes().getEntityId())
                    && !PonyConfig.getEffectiveRace(state.getAttributes().metadata.race()).isHuman()) {
                state.getAttributes().visualHeight = Minecraft.getInstance().player.getBbHeight();
            }
        });
        PonyDataCallback.EVENT.register((sender, _, env) -> {
            if (sender instanceof Scaling.Holder holder
                    && sender instanceof AbstractClientPlayer player
                    && BigPony.getInstance().getConfig().useDetectedPonyScaling.get()
                    && env == EnvType.CLIENT && BigPonyClient.isClientPlayer(player)) {
                detectPreset(player.getGameProfile()).thenAccept(holder.getScaling()::setDimensions);
            }
        });

        PonyConfig.getInstance().onChangedExternally(_ -> enforceFillyCamState());
        PonyConfig.getInstance().fillycam.onChanged(_ -> enforceFillyCamState());

        BigPonyClient.setIsPonyPredicate(
                state -> state instanceof PonyModel.AttributedHolder,
                entity -> Pony.getManager().getPony(entity).filter(pony -> !pony.race().isHuman()).isPresent()
        );
    }

    private synchronized void enforceFillyCamState() {
        if (switching) {
            return;
        }
        switching = true;

        if (isFillyCam() && BigPony.getInstance().getConfig().useDetectedPonyScaling.get()) {
            PonyConfig.getInstance().fillycam.set(false);
            GameGui.playSound(SoundEvents.VILLAGER_NO);

            var client = Minecraft.getInstance();

            if (client.player != null) {
                client.player.sendSystemMessage(Component.literal("[Big Pony] FillyCam was enabled! Auto-Detect function has been disabled").withStyle(ChatFormatting.DARK_RED));
            }

            BigPony.getInstance().getConfig().useDetectedPonyScaling.set(false);

            if (client.gui.screen() instanceof GuiBigSettings settingsScreen) {
                settingsScreen.toggleMLPScalingOff();
            } else {
                PresetDetector.getInstance().revertFillyCam();
                EntityScale dimensions = BigPony.getInstance().getConfig().scale.get();
                boolean scalingConsent = client.player == null || Permissions.freeform(InteractionManager.getInstance().getPermissions());
                dimensions = scalingConsent ? dimensions.withModel(dimensions.body()) : EntityScale.DEFAULT;
                BigPony.getInstance().getConfig().scale.set(dimensions);
                if (client.player instanceof Scaling.Holder holder) {
                    holder.getScaling().setDimensions(dimensions);
                }
            }
            BigPony.getInstance().getConfig().save();
        }

        switching = false;
    }

    public void setFillyCam(boolean enable) {
        boolean fillyCam = isFillyCam();
        if (enable == fillyCam) {
            return;
        }
        if (oldFillycam == null || fillyCam != setFillycam) {
            oldFillycam = fillyCam;
        }
        setFillycam = enable;
        PonyConfig.getInstance().fillycam.set(enable);
    }

    @Override
    public void revertFillyCam() {
        if (oldFillycam != null) {
            boolean fillyCam = isFillyCam();
            if (fillyCam == setFillycam && fillyCam != oldFillycam) {
                PonyConfig.getInstance().fillycam.set(oldFillycam);
            }
            oldFillycam = null;
        }
    }

    @Override
    public boolean isFillyCam() {
        return PonyConfig.getInstance().fillycam.get();
    }

    @Override
    public CompletableFuture<EntityScale> detectPreset(GameProfile profile) {
        return SkinDetecter.getInstance().loadSkin(profile).thenApplyAsync(skin -> {
            // Turn on filly cam so we can get the camera parameters
            boolean fillyCam = isFillyCam();
            synchronized (this) {
                switching = true;
                setFillyCam(true);

                Size size = Pony.getManager().getPony(skin).size();

                EntityScale scale = new EntityScale(
                        BodyScale.DEFAULT,
                        Optional.of(BodyScale.of(size.scaleFactor())),
                        new CameraScale(size.eyeDistanceFactor(), size.eyeHeightFactor())
                );

                // We turn off filly cam because it's not needed and might cause issues with buckets if left enabled
                setFillyCam(false);
                if (!fillyCam) {
                    PonyConfig.getInstance().save();
                }
                switching = false;
                return scale;
            }
        }, Minecraft.getInstance());
    }
}
