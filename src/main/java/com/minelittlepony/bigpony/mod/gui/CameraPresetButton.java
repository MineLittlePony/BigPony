package com.minelittlepony.bigpony.mod.gui;

import com.minelittlepony.bigpony.mod.CameraPresets;

import net.minecraft.client.gui.GuiButton;

public class CameraPresetButton extends GuiButton implements IPerformable {

    private final GuiBigSettings gui;

    private final CameraPresets preset;

    private final boolean camera, body;

    public CameraPresetButton(GuiBigSettings gui, CameraPresets preset, int left, int top, boolean camera, boolean body) {
        super(11, left, top + (20 * preset.ordinal()), (camera == body) && body ? 20 : 80, 20, (camera == body) && body ? "o" : preset.name());
        this.gui = gui;
        this.preset = preset;
        this.camera = camera;
        this.body = body;
    }

    @Override
    public void performAction() {
        gui.applyPreset(preset, camera, body);
    }
}
