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
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetHandler;
import net.minecraft.network.play.server.SPacketJoinGame;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import playersync.client.api.PlayerSync;
import playersync.client.api.SyncManager;

import java.io.File;
import javax.annotation.Nullable;

/**
 * Ah' am a big pony!
 */
public class LiteModBigPony implements BigPony, InitCompleteListener, Tickable, JoinGameListener {

    private static final String PSYNC = "playersync";

    private static final String NAME = "BigPony";
    private static final String CHANNEL = "bigpony";
    private static final String DATA = CHANNEL + "|scale";

    static Logger logger = LogManager.getLogger(NAME);
    
    public static LiteModBigPony instance() {
      return LiteLoader.getInstance().getMod(LiteModBigPony.class);
    }
    
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
        LiteLoader.getInstance().registerExposable(this, null);
        LiteLoader.getInput().registerKeyBinding(this.settingsBind);

        this.scale = new PlayerScaleM(xScale, yScale, zScale);

    }

    @Override
    public void onInitCompleted(Minecraft minecraft, LiteLoader loader) {

        if (loader.isModActive(PSYNC)) {
            manager = PlayerSync.getManager();
            manager.register(DATA, new PlayerScale.Serializer(), (chan, uuid, obj) -> sizes.handlePacket(uuid, obj), scale);

            logger.info("PlayerSync detected!");
        } else {
            logger.warn("PlayerSync not detected! Client synchronization will be disabled.");
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
        // initialize this when the player is available.
        // doing this earlier causes offline-mode to not work properly.
        this.sizes = new PlayerSizeManager(((NetHandlerPlayClient) netHandler).getGameProfile());
        this.sizes.setOwnScale(xScale, yScale, zScale);

        updateHeightDistance();
    }

    public void onRenderEntity(EntityLivingBase entity) {
        if (sizes != null && entity instanceof EntityPlayer) {
            this.sizes.onRenderPlayer((EntityPlayer) entity);
        }
    }

    public float getUpdatedShadowSize(float initial, Entity entity) {
        if (sizes == null || !(entity instanceof IEntityPlayer)) return initial;
        return initial * sizes.getShadowScale(((EntityPlayer)entity));
    }

    @Override
    public void upgradeSettings(String version, File configPath, File oldConfigPath) {

    }

    @Override
    public void setScale(float xScale, float yScale, float zScale) {
        if (xScale != this.xScale || yScale != this.yScale || zScale != this.zScale) {
            this.sizes.setOwnScale(xScale, yScale, zScale);

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

        updateHeightDistance();
    }

    @Override
    public float getHeight() {
        return height;
    }

    @Override
    public void setDistance(float distance) {
        this.distance = distance;
        LiteLoader.getInstance().writeConfig(this);

        updateHeightDistance();
    }

    @Override
    public float getDistance() {
        return distance;
    }

    private void updateHeightDistance() {
        Minecraft mc = Minecraft.getMinecraft();

        IEntityRenderer er = (IEntityRenderer) mc.entityRenderer;
        er.setThirdPersonDistance(distance);
        IEntityPlayer ep = (IEntityPlayer) mc.player;
        ep.setEyeHeight(height);

        if (mc.isIntegratedServerRunning()) {
            IEntityPlayer mplayer = (IEntityPlayer) mc.getIntegratedServer().getPlayerList().getPlayerByUUID(mc.player.getUniqueID());
            if (mplayer != null) mplayer.setEyeHeight(height);
        }
    }
}
