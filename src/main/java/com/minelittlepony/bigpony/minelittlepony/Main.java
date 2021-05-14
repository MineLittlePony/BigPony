package com.minelittlepony.bigpony.minelittlepony;

import com.minelittlepony.api.pony.meta.Size;
import com.minelittlepony.bigpony.Cam;
import com.minelittlepony.bigpony.Scaled;
import com.minelittlepony.bigpony.Scaling;
import com.minelittlepony.bigpony.Triple;
import com.minelittlepony.client.MineLittlePony;
import com.minelittlepony.api.model.IModel;
import com.minelittlepony.api.model.ModelAttributes;
import com.minelittlepony.api.model.fabric.PonyModelPrepareCallback;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

public class Main extends PresetDetector implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        INSTANCE = this;

        PonyModelPrepareCallback.EVENT.register(this::onPonyModelPrepared);
    }

    private void onPonyModelPrepared(Entity entity, IModel model, ModelAttributes.Mode mode) {

        if (entity instanceof Scaled && !((Scaled)entity).getScaling().isVisual() && isPony((PlayerEntity)entity)) {
            model.getAttributes().visualHeight = entity.getHeight() / model.getSize().getScaleFactor();
        }
    }

    @Override
    public boolean isFillyCam() {
        return MineLittlePony.getInstance().getConfig().fillycam.get();
    }

    @Override
    public boolean isPony(PlayerEntity player) {
        return !MineLittlePony.getInstance().getManager().getPony(player).getRace(false).isHuman();
    }

    @Override
    public void detectPreset(PlayerEntity player, Scaling into) {
        // Turn on filly cam so we can get the camera parameters
        MineLittlePony.getInstance().getConfig().fillycam.set(true);

        Size size = MineLittlePony.getInstance().getManager().getPony(player).getMetadata().getSize();

        into.setScale(new Triple(size.getScaleFactor()));
        into.setCamera(new Cam(size.getEyeDistanceFactor(), size.getEyeHeightFactor()));

        // We turn off filly cam because it's not needed and might cause issues with buckets if let enabled
        MineLittlePony.getInstance().getConfig().fillycam.set(false);
        MineLittlePony.getInstance().getConfig().save();
    }

}
