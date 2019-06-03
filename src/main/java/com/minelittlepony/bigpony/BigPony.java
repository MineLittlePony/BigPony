package com.minelittlepony.bigpony;

import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

import com.minelittlepony.bigpony.ducks.IEntityPlayer;
import com.minelittlepony.bigpony.gui.GuiBigSettings;
import com.minelittlepony.bigpony.scale.IPlayerScale;
import com.minelittlepony.common.client.IModUtilities;
import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

public class BigPony {

    public static final Logger logger = LogManager.getLogger("BigPony");

    private static BigPony instance;

    private KeyBinding settingsBind;

    private final JsonConfig config;

    public static BigPony getInstance() {
        return instance;
    }

    public JsonConfig getConfig() {
        return config;
    }

    public BigPony(IModUtilities utils) {
        instance = this;
        config = JsonConfig.of(utils.getConfigDirectory().resolve("bigpony.json"));

        settingsBind = utils.registerKeybind("key.category.bigpony", GLFW.GLFW_KEY_F10, "bigpony.settings");
    }

    public void onClientTick(MinecraftClient minecraft) {
        if (settingsBind.isPressed()) {
            minecraft.openScreen(new GuiBigSettings(config.data));
        }
    }

    public void onRenderEntity(LivingEntity entity) {
        if (entity instanceof IEntityPlayer) {
            IPlayerScale scale = ((IEntityPlayer)entity).getPlayerScale();
            GlStateManager.scalef(scale.getXScale(), scale.getYScale(), scale.getZScale());
        }
    }

    public void onRenderShadow(float initial, Entity entity, Consumer<Float> shadowRender) {
        if (!(entity instanceof IEntityPlayer)) {
            shadowRender.accept(initial);
        } else {
            GlStateManager.pushMatrix();
            // The shadows render a tiny bit off the ground, so let's shift it down so as not to cut through people's legs.
            GlStateManager.translatef(0, -0.01F, 0);
            shadowRender.accept(initial * ((IEntityPlayer)entity).getPlayerScale().getShadowScale());
            GlStateManager.popMatrix();
        }
    }
}
