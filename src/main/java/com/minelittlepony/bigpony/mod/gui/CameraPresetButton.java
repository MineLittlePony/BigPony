package com.minelittlepony.bigpony.mod.gui;

import java.util.List;

import com.minelittlepony.bigpony.mod.CameraPresets;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;

public class CameraPresetButton {
    
    private final GuiBigSettings gui;

    private final CameraPresets preset;
    
    private final Button camera;
    private final Button scale;
    private final Button combined;
    
    public CameraPresetButton(List<GuiButton> buttons, GuiBigSettings gui, CameraPresets preset, int right) {
        this.gui = gui;
        this.preset = preset;
        
        buttons.add(camera = new Button(right + 100, 40, true, false));
        buttons.add(scale = new Button(right + 80, 40, false, true));
        buttons.add(combined = new Button(right, 40, true, true));
    }
    
    public void updateEnabled(float height, float distance, float xSize, float ySize, float zSize) {
        camera.enabled = !preset.isEqual(height, distance);
        scale.enabled = !preset.isEqual(xSize, ySize, zSize);
        combined.enabled = camera.enabled || scale.enabled;
    }
    
    private class Button extends GuiButton implements IPerformable {

        private final boolean camera, body;

        public Button(int left, int top, boolean camera, boolean body) {
            super(11, left, 0, 20, 20, "");
            
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
}
