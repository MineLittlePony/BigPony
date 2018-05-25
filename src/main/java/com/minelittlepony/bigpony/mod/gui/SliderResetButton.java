package com.minelittlepony.bigpony.mod.gui;

import com.minelittlepony.bigpony.mod.FloatUtils;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiSlider;

public class SliderResetButton extends GuiButton implements IPerformable {

    private final GuiSlider slider;
    
    private final float def;

    public SliderResetButton(GuiSlider slider, float def) {
        super(12, slider.x + slider.getButtonWidth() + 5, slider.y, 20, 20, "*");
        this.slider = slider;
        this.def = def;
    }

    public void updateEnabled() {
        enabled = !FloatUtils.equals(slider.getSliderValue(), def);
    }

    @Override
    public void performAction() {
        slider.setSliderValue(1, true);
    }
}
