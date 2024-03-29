package com.minelittlepony.bigpony.client.gui;

import com.minelittlepony.common.client.gui.element.Button;

public class CameraPresetButton {

    private final GuiBigSettings gui;

    private final CameraPresets preset;

    private final PresetButton camera;
    private final PresetButton scale;
    private final PresetButton combined;

    public CameraPresetButton(GuiBigSettings gui, CameraPresets preset, int right) {
        this.gui = gui;
        this.preset = preset;

        int y = 20 + (20 * preset.ordinal());

        gui.content.addButton(camera = new PresetButton(right + 100, y, 20, true, false, "c"));
        gui.content.addButton(scale = new PresetButton(right + 80, y, 20, false, true, "b"));
        gui.content.addButton(combined = new PresetButton(right, y, 80, true, true, "minebp.presets." + preset.name().toLowerCase()));
    }

    public void updateEnabled(float height, float distance, float xSize, float ySize, float zSize) {
        camera.setEnabled(!preset.isEqual(height, distance) && gui.hasScalingConsent());
        scale.setEnabled(!preset.isEqual(xSize, ySize, zSize) && gui.hasScalingConsent());
        combined.setEnabled((camera.active || scale.active) && gui.hasScalingConsent());
        if (!gui.hasCameraConsent()) {
            camera.setEnabled(false);
        }
    }

    private class PresetButton extends Button {
        public PresetButton(int x, int y, int width, boolean camera, boolean body, String label) {
            super(x, y, width, 20);
            onClick(self -> gui.applyPreset(preset, camera, body));
            getStyle().setText(label);
        }
    }
}
