package com.minelittlepony.bigpony.mod.gui;

import com.minelittlepony.bigpony.mod.BigPony;
import com.minelittlepony.bigpony.mod.CameraPresets;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiPageButtonList.GuiResponder;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlider.FormatHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.MathHelper;

import java.io.IOException;
import javax.annotation.Nonnull;

public class GuiBigSettings extends GuiScreen implements GuiResponder, FormatHelper {

    private BigPony bigPony;

    private ResettableSlider allSize, xSize, ySize, zSize, height, distance;

    private Checkbox auto;
    
    private CameraPresetButton[] presets;

    public GuiBigSettings(BigPony bigPony) {
        this.bigPony = bigPony;
    }

    @Override
    public void initGui() {
        int top = 20;
        int left = width / 2 - 200;
        int right = width / 2 + 50;
        
        allSize = new ResettableSlider(buttonList, this, 1, left, top += 20, "minebp.scale.global", .1F, 2F, bigPony.getXScale(), this);
        xSize = new ResettableSlider(buttonList, this, 2, left, top += 20, "minebp.scale.x", .1F, 2F, bigPony.getXScale(), this);
        ySize = new ResettableSlider(buttonList, this, 3, left, top += 20, "minebp.scale.y", .1F, 2F, bigPony.getYScale(), this);
        zSize = new ResettableSlider(buttonList, this, 4, left, top += 20, "minebp.scale.z", .1F, 2F, bigPony.getZScale(), this);
        
        top += 20;
        
        height = new ResettableSlider(buttonList, this, 5, left, top += 20, "minebp.camera.height", .1F, 2F, bigPony.getHeight(), this);
        distance = new ResettableSlider(buttonList, this, 6, left, top += 20, "minebp.camera.distance", .1F, 2F, bigPony.getDistance(), this);
        
        top += 20;
        
        this.buttonList.add(auto = new Checkbox(left, top += 20, "minebp.camera.auto", bigPony.autoDetect(), bigPony::setAutoDetect));
        
        CameraPresets[] values = CameraPresets.values();

        presets = new CameraPresetButton[values.length];

        for (int i = 0; i < presets.length; i++) {
            presets[i] = new CameraPresetButton(buttonList, this, values[i], right);
        }
        
        updateScreen();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        
        int left = width / 2 - 200;
        int right = width / 2 + 16;
        
        drawCenteredString(fontRenderer, I18n.format("minebp.options.title"), width / 2, 10, -1);
        drawCenteredString(fontRenderer, I18n.format("minebp.options.body"), left + 80, 25, -1);
        drawCenteredString(fontRenderer, I18n.format("minebp.options.camera"), left + 80, 125, -1);
        drawCenteredString(fontRenderer, I18n.format("minebp.options.presets"), right + 90, 25, -1);
    }

    @Override
    public void updateScreen() {
        for (int i = 0; i < presets.length; i++) {
            presets[i].updateEnabled(height.getSliderValue(), distance.getSliderValue(), xSize.getSliderValue(), ySize.getSliderValue(), zSize.getSliderValue());
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button instanceof IPerformable) {
            ((IPerformable)button).performAction();
        } else if (button.id == auto.id) {
            auto.checked = !auto.checked;
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
        float x = bigPony.getXScale();
        float y = bigPony.getYScale();
        float z = bigPony.getZScale();
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
