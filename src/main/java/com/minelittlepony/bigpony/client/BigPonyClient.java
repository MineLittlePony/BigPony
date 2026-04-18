package com.minelittlepony.bigpony.client;

import java.util.UUID;
import java.util.function.Predicate;

import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import com.google.common.base.Predicates;
import com.minelittlepony.bigpony.BigPony;
import com.minelittlepony.bigpony.client.gui.GuiBigSettings;
import com.minelittlepony.bigpony.network.client.ClientNetworkHandlerImpl;
import com.mojang.blaze3d.platform.InputConstants;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.LivingEntity;

public class BigPonyClient implements ClientModInitializer {
    private static BigPonyClient instance;

    private final KeyMapping keybind = new KeyMapping("key.minebp.settings", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_F8, KeyMapping.Category.MISC);

    private static Predicate<EntityRenderState> isPonyPredicate = Predicates.alwaysFalse();
    private static Predicate<LivingEntity> isEntityPonyPredicate = Predicates.alwaysFalse();

    public static BigPonyClient getInstance() {
        return instance;
    }

    public static boolean isPony(EntityRenderState state) {
        return isPonyPredicate.test(state);
    }

    public static boolean isPony(LivingEntity entity) {
        return isEntityPonyPredicate.test(entity);
    }

    public static void setIsPonyPredicate(Predicate<EntityRenderState> statePredicate, Predicate<LivingEntity> entityPredicate) {
        isPonyPredicate = statePredicate;
        isEntityPonyPredicate = entityPredicate;
    }

    public static boolean isClientPlayer(Avatar player) {
        return Minecraft.getInstance().player == player;
    }

    public static boolean isClientPlayer(@Nullable UUID id) {
        return Minecraft.getInstance().player != null && Minecraft.getInstance().player.getUUID().equals(id);
    }

    public BigPonyClient() {
        instance = this;
    }

    @Override
    public void onInitializeClient() {
        KeyMappingHelper.registerKeyMapping(keybind);
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (keybind.isDown()) {
                client.setScreen(new GuiBigSettings(client.screen));
            }
        });
        new ClientNetworkHandlerImpl();

        BigPony.getInstance().getConfig().onChangedExternally(_ -> {
            Minecraft.getInstance().execute(() -> {
                if (Minecraft.getInstance().screen instanceof GuiBigSettings screen) {
                    screen.init(screen.width, ((Screen)screen).height);
                }
            });
        });
    }
}
