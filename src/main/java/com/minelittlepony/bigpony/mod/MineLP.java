package com.minelittlepony.bigpony.mod;

import com.minelittlepony.MineLittlePony;
import com.minelittlepony.pony.data.IPony;
import com.minelittlepony.pony.data.PonyRace;
import com.minelittlepony.pony.data.PonySize;
import com.mumfrey.liteloader.core.LiteLoader;

import net.minecraft.client.Minecraft;

public final class MineLP {
    private static final String MOD_NAME = "minelittlepony";
    
    /**
     * Returns true if mine little pony is present. That's all we need.
     */
    public static boolean modIsActive() {
        return LiteLoader.getInstance().isModActive(MOD_NAME);
    }
    
    public static float adjustForShowAccuracy(float amount) {
        if (!modIsActive()) {
            return amount;
        }
        return MineLittlePony.getConfig().getGlobalScaleFactor() * amount;
    }
    
    public static CameraPresets getEffectivePreset() {
        if (!modIsActive()) {
            return CameraPresets.HUMAN;
        }
        
        IPony pony = IPony.forPlayer(Minecraft.getMinecraft().player);
        
        PonyRace race = pony.getRace(false);
        PonySize size = pony.getMetadata().getSize();
        
        if (race.isHuman()) {
            return CameraPresets.HUMAN;
        }
        if (size == PonySize.FOAL) {
            return CameraPresets.FILLY;
        }
        if (size == PonySize.TALL) {
            return CameraPresets.ALICORN;
        }
        if (size == PonySize.BULKY || size == PonySize.LANKY) {
            return CameraPresets.STALLION;
        }
        
        return CameraPresets.MARE;
    }
}
