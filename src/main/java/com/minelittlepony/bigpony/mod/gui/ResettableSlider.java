package com.minelittlepony.bigpony.mod.gui;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiSlider;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiPageButtonList.GuiResponder;

public class ResettableSlider extends GuiSlider {

    public SliderResetButton reset;

    public ResettableSlider(List<GuiButton> buttonList, GuiResponder responder, int id, int left, int top, String title, float min, float max, float def, FormatHelper formatter) {
        super(responder, id, left, top, I18n.format(title), min, max, def, formatter);
        reset = new SliderResetButton(this);
        buttonList.add(this);
        buttonList.add(reset);
        reset.updateEnabled();
    }

    @Override
    public void setSliderValue(float value, boolean notifyResponder) {
        super.setSliderValue(value, notifyResponder);
        reset.updateEnabled();
    }

    @Override
    public void setSliderPosition(float position) {
        super.setSliderPosition(position);
        reset.updateEnabled();
    }

    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        boolean res = super.mousePressed(mc, mouseX, mouseY);
        if (res) reset.updateEnabled();
        return res;
    }
    
    @Override
    protected void mouseDragged(Minecraft mc, int mouseX, int mouseY) {
        super.mouseDragged(mc, mouseX, mouseY);
        if (isMouseDown) {
            reset.updateEnabled();
        }
    }
}
