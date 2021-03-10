package com.minelittlepony.bigpony.client.gui;

import com.minelittlepony.common.client.gui.GameGui;
import com.minelittlepony.common.client.gui.element.Button;
import com.minelittlepony.common.client.gui.element.Slider;

public class ResettableSlider extends Slider {

    public final Button reset;

    public ResettableSlider(GameGui gui, int left, int top, float min, float max, float value) {
        super(left, top, min, max, Math.min(value, max));

        gui.addButton(reset = new Button(left + width + 5, top, 20, 20)
                .onClick(o -> setValue(1F))
                .setEnabled(!FloatUtils.equals(getValue(), 1)))
                .getStyle().setText("x");
    }

    @Override
    protected void setClampedValue(float value) {
        super.setClampedValue(value);
        reset.setEnabled(!FloatUtils.equals(getValue(), 1));
    }
}
