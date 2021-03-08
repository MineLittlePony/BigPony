package com.minelittlepony.bigpony;

import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

import com.minelittlepony.bigpony.gui.GuiBigSettings;
import com.minelittlepony.common.util.GamePaths;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

public class BigPony implements ClientModInitializer {

    public static final Logger logger = LogManager.getLogger("BigPony");

    private static BigPony instance;

    private KeyBinding settingsBind = new KeyBinding("key.category.misc", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_F10, "bigpony:settings");

    private final Config config = new Config(GamePaths.getConfigDirectory().resolve("bigpony.json"));

    public static BigPony getInstance() {
        return instance;
    }

    public Config getConfig() {
        return config;
    }

    public Scaling getScaling() {
        return config.scale.get();
    }

    public BigPony() {
        instance = this;
    }

    @Override
    public void onInitializeClient() {
        KeyBindingHelper.registerKeyBinding(settingsBind);
        ClientTickEvents.END_CLIENT_TICK.register(this::onClientTick);
    }

    private void onClientTick(MinecraftClient client) {
        if (settingsBind.isPressed()) {
            client.openScreen(new GuiBigSettings(getScaling()));
        }
    }

    public void onRenderEntity(LivingEntity entity, MatrixStack stack) {
        if (entity instanceof Scaled) {
            Scaling scale = ((Scaled)entity).getScaling();
            stack.scale(scale.getScale().x, scale.getScale().x, scale.getScale().x);
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
