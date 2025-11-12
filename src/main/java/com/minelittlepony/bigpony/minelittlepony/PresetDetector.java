package com.minelittlepony.bigpony.minelittlepony;

import java.util.concurrent.CompletableFuture;

import com.minelittlepony.bigpony.data.EntityScale;
import com.mojang.authlib.GameProfile;

import net.minecraft.entity.LivingEntity;

public class PresetDetector {
    static PresetDetector INSTANCE = new PresetDetector();

    public static PresetDetector getInstance() {
        return INSTANCE;
    }

    PresetDetector() {}

    public void revertFillyCam() {

    }

    public boolean isFillyCam() {
        return false;
    }

    public boolean isPony(LivingEntity entity) {
        return false;
    }

    public CompletableFuture<EntityScale> detectPreset(GameProfile profile) {
        return CompletableFuture.failedFuture(null);
    }
}
