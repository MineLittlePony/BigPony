package com.minelittlepony.bigpony.minelittlepony;

import com.minelittlepony.bigpony.Scaling;
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

    public void detectPreset(PlayerEntity player, Scaling into) {
    }
}
