package com.minelittlepony.bigpony.mod.gui;

import com.minelittlepony.bigpony.mod.CameraPresets;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;

public class CameraPresetButton extends GuiButton implements IPerformable {

    private final GuiBigSettings gui;

    private final CameraPresets preset;

    private final boolean camera, body;

    public CameraPresetButton(GuiBigSettings gui, CameraPresets preset, int left, int top, boolean camera, boolean body) {
        super(11, left, 0, 20, 20, "");
        this.gui = gui;
        this.preset = preset;
        this.camera = camera;
        this.body = body;
        
        boolean both = (camera == body) && body;
        
        this.y = top + (20 * preset.ordinal());
        this.width = both ? 80 : 20;
        this.displayString = both ? I18n.format("minebp.presets." + preset.name().toLowerCase()) : camera ? "c" : "b";
    }

    @Override
    public void performAction() {
        gui.applyPreset(preset, camera, body);
    }
}
