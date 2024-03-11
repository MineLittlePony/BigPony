package com.minelittlepony.bigpony.client;

import org.lwjgl.glfw.GLFW;

import com.minelittlepony.bigpony.Scaled;
import com.minelittlepony.bigpony.client.gui.GuiBigSettings;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
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
        ClientTickEvents.END_CLIENT_TICK.register(this::onClientTick);
    }

    private void onClientTick(MinecraftClient client) {
        if (keybind.isPressed()) {
            client.setScreen(new GuiBigSettings(client.currentScreen));
        }
    }

    public float onRenderShadow(float radius, Entity entity, MatrixStack stack) {
        return radius * (entity instanceof Scaled ? ((Scaled)entity).getScaling().getShadowScale() : 1);
    }
}
