package com.minelittlepony.bigpony.client;

import java.util.function.Consumer;

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

    private KeyBinding keybind = new KeyBinding("key.category.misc", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_F10, "bigpony:settings");

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
        KeyBindingHelper.registerKeyBinding(keybind);
        ClientTickEvents.END_CLIENT_TICK.register(this::onClientTick);
    }

    private void onClientTick(MinecraftClient client) {
        if (keybind.isPressed()) {
            client.setScreen(new GuiBigSettings(client.currentScreen));
        }
    }

    public void onRenderShadow(float initial, Entity entity, MatrixStack stack, Consumer<Float> shadowRender) {
        if (!(entity instanceof Scaled)) {
            shadowRender.accept(initial);
        } else {
            stack.push();
            // The shadows render a tiny bit off the ground, so let's shift it down so as not to cut through people's legs.
            stack.translate(0, -0.01F, 0);
            shadowRender.accept(initial * ((Scaled)entity).getScaling().getShadowScale());
            stack.pop();
        }
    }
}
