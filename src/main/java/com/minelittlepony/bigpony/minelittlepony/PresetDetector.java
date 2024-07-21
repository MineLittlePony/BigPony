package com.minelittlepony.bigpony.minelittlepony;

import java.util.concurrent.CompletableFuture;

import com.minelittlepony.bigpony.data.EntityScale;
import com.mojang.authlib.GameProfile;

import net.minecraft.entity.player.PlayerEntity;

public class PresetDetector {
    static PresetDetector INSTANCE = new PresetDetector();

    public static PresetDetector getInstance() {
        return INSTANCE;
    }

    PresetDetector() {}

    public boolean isFillyCam() {
        return false;
    }

    public boolean isPony(PlayerEntity player) {
        return false;
    }

    public CompletableFuture<EntityScale> detectPreset(GameProfile profile) {
        return CompletableFuture.failedFuture(null);
    }
}
