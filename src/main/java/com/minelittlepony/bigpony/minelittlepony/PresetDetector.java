package com.minelittlepony.bigpony.minelittlepony;

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
}
