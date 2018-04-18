package com.minelittlepony.bigpony.mod;

import com.google.gson.annotations.Expose;
import com.minelittlepony.bigpony.mod.ducks.IEntityPlayer;
import com.minelittlepony.bigpony.mod.ducks.IEntityRenderer;
import com.minelittlepony.bigpony.mod.gui.GuiBigSettings;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mumfrey.liteloader.InitCompleteListener;
import com.mumfrey.liteloader.JoinGameListener;
import com.mumfrey.liteloader.Tickable;
import com.mumfrey.liteloader.core.LiteLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.INetHandler;
import net.minecraft.network.play.server.SPacketJoinGame;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

import java.io.File;

/**
 * Ah' am a big pony!
 */
public class LiteModBigPony implements BigPony, InitCompleteListener, Tickable, JoinGameListener {

    private static final String NAME = "BigPony";

    static final Logger logger = LogManager.getLogger(NAME);
    
    public static LiteModBigPony instance() {
        return LiteLoader.getInstance().getMod(LiteModBigPony.class);
    }

    private PlayerSizeManager sizes;

    private final KeyBinding settingsBind = new KeyBinding("bigpony.settings", Keyboard.KEY_F10, "key.category.bigpony");

    @Expose
    private float height = 1F;
    @Expose
    private float distance = 1F;
    @Expose
    private float xScale = 1F;
    @Expose
    private float yScale = 1F;
    @Expose
    private float zScale = 1F;

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getVersion() {
        return "@VERSION@";
    }

    @Override
    public void init(File configPath) {
        LiteLoader.getInstance().registerExposable(this, null);
        LiteLoader.getInput().registerKeyBinding(settingsBind);
    }

    @Override
    public void onInitCompleted(Minecraft minecraft, LiteLoader loader) {
      sizes = new PlayerSizeManager(loader, this);
    }

    @Override
    public void onTick(Minecraft minecraft, float partialTicks, boolean inGame, boolean clock) {
        if (settingsBind.isPressed()) {
            minecraft.displayGuiScreen(new GuiBigSettings(this));
        }
    }

    @Override
    public void onJoinGame(INetHandler netHandler, SPacketJoinGame joinGamePacket, ServerData serverData, RealmsServer realmsServer) {
        updateHeightAndDistance();
    }

    public void onRenderEntity(EntityLivingBase entity) {
        if (entity instanceof IEntityPlayer) {
            IPlayerScale scale = ((IEntityPlayer)entity).getPlayerScale();
            GlStateManager.scale(scale.getXScale(), scale.getYScale(), scale.getZScale());
        }
    }

    public float getUpdatedShadowSize(float initial, Entity entity) {
        if (sizes == null || !(entity instanceof IEntityPlayer)) return initial;
        return initial * sizes.getShadowScale(((IEntityPlayer)entity));
    }
    
    @Override
    public void upgradeSettings(String version, File configPath, File oldConfigPath) {
    }

    @Override
    public void setScale(float xScale, float yScale, float zScale) {
        if (xScale != this.xScale || yScale != this.yScale || zScale != this.zScale) {
            this.xScale = xScale;
            this.yScale = yScale;
            this.zScale = zScale;

            updateHeightAndDistance();
        }
    }

    @Override
    public float getXScale() {
        return xScale;
    }

    @Override
    public float getYScale() {
        return yScale;
    }

    @Override
    public float getZScale() {
        return zScale;
    }

    @Override
    public void setHeight(float height) {
        this.height = height;
        updateHeightAndDistance();
    }

    @Override
    public float getHeight() {
        return height;
    }

    @Override
    public void setDistance(float distance) {
        this.distance = distance;
        updateHeightAndDistance();
    }

    @Override
    public float getDistance() {
        return distance;
    }

    private void updateHeightAndDistance() {
        LiteLoader.getInstance().writeConfig(this);

        Minecraft mc = Minecraft.getMinecraft();

        ((IEntityRenderer) mc.entityRenderer).setThirdPersonDistance(distance);

        sizes.setScale((IEntityPlayer) mc.player, this);
    }

    @Override
    public void copyFrom(IPlayerScale scale) {

    }
}
