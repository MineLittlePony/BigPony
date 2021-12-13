package com.minelittlepony.bigpony.minelittlepony;

import java.util.concurrent.CompletableFuture;

import com.minelittlepony.bigpony.Scaling;
import com.mojang.authlib.GameProfile;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

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

    public CompletableFuture<Identifier> detectPreset(GameProfile profile, Scaling into) {
        return CompletableFuture.failedFuture(null);
    }
}
