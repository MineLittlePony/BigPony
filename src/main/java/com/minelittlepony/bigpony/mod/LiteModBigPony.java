package com.minelittlepony.bigpony.mod;

import com.google.common.base.Throwables;
import com.google.gson.annotations.Expose;
import com.minelittlepony.bigpony.mod.gui.GuiBigSettings;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mumfrey.liteloader.InitCompleteListener;
import com.mumfrey.liteloader.JoinGameListener;
import com.mumfrey.liteloader.Tickable;
import com.mumfrey.liteloader.core.LiteLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetHandler;
import net.minecraft.network.play.server.SPacketJoinGame;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import playersync.client.api.IPlayerSync;
import playersync.client.api.SyncManager;

import javax.activity.InvalidActivityException;
import javax.annotation.Nullable;
import java.io.File;

/**
 * Ah' am a big pony!
 */
public class LiteModBigPony implements BigPony, InitCompleteListener, Tickable, JoinGameListener {

    private static final String PSYNC = "playersync";

    private static final String NAME = "BigPony";
    private static final String CHANNEL = "bigpony";
    private static final String DATA = CHANNEL + "|scale";

    static Logger logger = LogManager.getLogger(NAME);

    @Nullable
    private SyncManager manager;
    private PlayerSizeManager sizes;

    private KeyBinding settingsBind = new KeyBinding("bigpony.settings", Keyboard.KEY_F10, "key.category.bigpony");

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
    private PlayerScaleM scale;

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
        Minecraft mc = Minecraft.getMinecraft();
        this.sizes = new PlayerSizeManager(mc.getSession().getProfile());

        LiteLoader.getInstance().registerExposable(this, null);
        LiteLoader.getInput().registerKeyBinding(this.settingsBind);

        this.scale = new PlayerScaleM(xScale, yScale, zScale);

    }

    @Override
    public void onInitCompleted(Minecraft minecraft, LiteLoader loader) {
        try {
            if (loader.isModActive(PSYNC)) {
                IPlayerSync sync = LiteLoader.getInstance().getMod(PSYNC);
                manager = sync.getPlayerSyncManager();
                manager.register(DATA, new PlayerScale.Serializer(), new ScaleHandler(this.sizes), scale);

                logger.info("PlayerSync detected!");
            } else {
                logger.warn("PlayerSync not detected!");
            }
        } catch (InvalidActivityException e) {
            // this should be a runtime exception anyway
            Throwables.propagate(e);
        }
    }

    @Override
    public void onTick(Minecraft minecraft, float partialTicks, boolean inGame, boolean clock) {
        if (settingsBind.isPressed()) {

            minecraft.displayGuiScreen(new GuiBigSettings(this));
        }
    }

    @Override
    public void onJoinGame(INetHandler netHandler, SPacketJoinGame joinGamePacket, ServerData serverData, RealmsServer realmsServer) {
        this.sizes.clearPlayers();
        this.sizes.setScale(xScale, yScale, zScale);

    }

    public void onRenderEntity(EntityLivingBase entity) {
        if (entity instanceof EntityPlayer)
            this.sizes.onRenderPlayer((EntityPlayer) entity);
    }

    public float getEyeHeight(float initialHeight) {
        return initialHeight * height;
    }

    public float getCameraDistance(float initialDistance) {
        return initialDistance * distance;
    }

    @Override
    public void upgradeSettings(String version, File configPath, File oldConfigPath) {

    }

    @Override
    public void setScale(float xScale, float yScale, float zScale) {
        if (xScale != this.xScale || yScale != this.yScale || zScale != this.zScale) {
            this.sizes.setScale(xScale, yScale, zScale);

            this.xScale = xScale;
            this.yScale = yScale;
            this.zScale = zScale;

            this.scale.set(xScale, yScale, zScale);

            LiteLoader.getInstance().writeConfig(this);

            if (manager != null) {
                manager.sendPacket(DATA, this.scale);
            }
        }
    }

    @Override
    public float getxScale() {
        return xScale;
    }

    @Override
    public float getyScale() {
        return yScale;
    }

    @Override
    public float getzScale() {
        return zScale;
    }

    @Override
    public void setHeight(float height) {
        this.height = height;
        LiteLoader.getInstance().writeConfig(this);
    }

    @Override
    public float getHeight() {
        return height;
    }

    @Override
    public void setDistance(float distance) {
        this.distance = distance;
        LiteLoader.getInstance().writeConfig(this);
    }

    @Override
    public float getDistance() {
        return distance;
    }
}
