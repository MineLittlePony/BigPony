package com.minelittlepony.bigpony.mod.gui;

import com.minelittlepony.bigpony.mod.BigPony;
import com.minelittlepony.bigpony.mod.CameraPresets;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiPageButtonList.GuiResponder;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlider.FormatHelper;
import net.minecraft.util.math.MathHelper;

import java.io.IOException;
import javax.annotation.Nonnull;

public class GuiBigSettings extends GuiScreen implements GuiResponder, FormatHelper {

    private BigPony bigPony;

    private ResettableSlider allSize, xSize, ySize, zSize, height, distance;

    private CameraPresetButton[] cameraPresets, scalePresets, combinedPresets;

    public GuiBigSettings(BigPony bigPony) {
        this.bigPony = bigPony;
    }

    @Override
    public void initGui() {
        // sliders
        int top = 20;
        int left = width / 2 - 200;
        int right = width / 2 + 16;
        
        allSize = new ResettableSlider(buttonList, this, 1, left, top += 20, "Global Scale", .1F, 2F, bigPony.getxScale(), this);
        xSize = new ResettableSlider(buttonList, this, 2, left, top += 20, "X Scale", .1F, 2F, bigPony.getxScale(), this);
        ySize = new ResettableSlider(buttonList, this, 3, left, top += 20, "Y Scale", .1F, 2F, bigPony.getyScale(), this);
        zSize = new ResettableSlider(buttonList, this, 4, left, top += 20, "Z Scale", .1F, 2F, bigPony.getzScale(), this);

        height = new ResettableSlider(buttonList, this, 5, left, top += 20, "Eye Height", .1F, 2F, bigPony.getHeight(), this);
        distance = new ResettableSlider(buttonList, this, 6, left, top += 20, "Camera Distance", .1F, 2F, bigPony.getDistance(), this);

        CameraPresets[] values = CameraPresets.values();
        // presets

        cameraPresets = new CameraPresetButton[values.length];
        scalePresets = new CameraPresetButton[values.length];
        combinedPresets = new CameraPresetButton[values.length];

        for (int i = 0; i < cameraPresets.length; i++) {
            buttonList.add(cameraPresets[i] = new CameraPresetButton(this, values[i], right, 40, true, false));
            buttonList.add(scalePresets[i] = new CameraPresetButton(this, values[i], right + 100, 40, false, true));
            buttonList.add(combinedPresets[i] = new CameraPresetButton(this, values[i], right + 80, 40, true, true));
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        
        int left = width / 2 - 200;
        int right = width / 2 + 16;
        
        drawCenteredString(fontRenderer, "BigPony settings", width / 2, 10, -1);
        drawCenteredString(fontRenderer, "Camera Presets", left + 80, 25, -1);
        drawCenteredString(fontRenderer, "Body Presets", right + 90, 25, -1);
    }

    @Override
    public void updateScreen() {
        CameraPresets[] values = CameraPresets.values();
        for (int i = 0; i < cameraPresets.length; i++) {
            cameraPresets[i].enabled = !values[i].isEqual(height.getSliderValue(), distance.getSliderValue());
            scalePresets[i].enabled = !values[i].isEqual(xSize.getSliderValue(), ySize.getSliderValue(), zSize.getSliderValue());
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button instanceof IPerformable) {
            ((IPerformable)button).performAction();
        }
    }

    public void applyPreset(CameraPresets preset, boolean camera, boolean body) {
        float h = preset.getHeight();
        if (body) {
            xSize.setSliderValue(h, true);
            ySize.setSliderValue(h, true);
            zSize.setSliderValue(h, true);
        }
        if (camera) {
            height.setSliderValue(h, true);
            distance.setSliderValue(preset.getDistance(), true);
        }
    }

    @Override
    public void setEntryValue(int id, boolean value) {

    }

    @Override
    public void setEntryValue(int id, float value) {
        float x = bigPony.getxScale();
        float y = bigPony.getyScale();
        float z = bigPony.getzScale();
        switch (id) {
            case 1:
                bigPony.setScale(value, value, value);
                bigPony.setHeight(value);
                xSize.setSliderValue(value, false);
                ySize.setSliderValue(value, false);
                zSize.setSliderValue(value, false);
                height.setSliderValue(value, false);
                break;
            case 2:
                bigPony.setScale(value, y, z);
                break;
            case 3:
                bigPony.setScale(x, value, z);
                break;
            case 4:
                bigPony.setScale(x, y, value);
                break;
            case 5:
                bigPony.setHeight(value);
                break;
            case 6:
                bigPony.setDistance(value);
                break;
        }
    }

    @Override
    public void setEntryValue(int id, @Nonnull String value) {

    }

    @Override
    @Nonnull
    public String getText(int id, @Nonnull String name, float value) {
        return String.format("%s: %d%%", name, MathHelper.ceil(value * 100));
    }
}
