package com.minelittlepony.bigpony.mod.gui;

import com.minelittlepony.bigpony.mod.FloatUtils;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiSlider;

public class SliderResetButton extends GuiButton implements IPerformable {

    private final GuiSlider slider;

    public SliderResetButton(GuiSlider slider) {
        super(12, slider.x + slider.getButtonWidth() + 5, slider.y, 20, 20, "*");
        this.slider = slider;
    }

    public void updateEnabled() {
        enabled = !FloatUtils.equals(slider.getSliderValue(), 1);
    }

    @Override
    public void performAction() {
        slider.setSliderValue(1, true);
    }
}
