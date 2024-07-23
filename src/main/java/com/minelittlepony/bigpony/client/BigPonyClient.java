package com.minelittlepony.bigpony.client;

import org.lwjgl.glfw.GLFW;

import com.minelittlepony.bigpony.BigPony;
import com.minelittlepony.bigpony.client.gui.GuiBigSettings;
import com.minelittlepony.bigpony.network.client.ClientNetworkHandlerImpl;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;

public class BigPonyClient implements ClientModInitializer {
    private static BigPonyClient instance;

    private KeyBinding keybind;

    public static BigPonyClient getInstance() {
        return instance;
    }

    public static boolean isClientPlayer(PlayerEntity player) {
        return MinecraftClient.getInstance().player == player;
    }

    public BigPonyClient() {
        instance = this;
    }

    @Override
    public void onInitializeClient() {
        keybind = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.minebp.settings", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_F8, KeyBinding.MISC_CATEGORY));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (keybind.isPressed()) {
                client.setScreen(new GuiBigSettings(client.currentScreen));
            }
        });
        new ClientNetworkHandlerImpl();

        BigPony.getInstance().getConfig().onChangedExternally(config -> {
            MinecraftClient.getInstance().execute(() -> {
                if (MinecraftClient.getInstance().currentScreen instanceof GuiBigSettings screen) {
                    screen.init(MinecraftClient.getInstance(), screen.width, ((Screen)screen).height);
                }
            });
        });
    }
}
