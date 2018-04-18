package com.minelittlepony.bigpony.mod.gui;

import com.minelittlepony.bigpony.mod.BigPony;
import com.minelittlepony.bigpony.mod.CameraPresets;
import com.minelittlepony.bigpony.mod.FloatUtils;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiPageButtonList.GuiResponder;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlider;
import net.minecraft.client.gui.GuiSlider.FormatHelper;
import net.minecraft.util.math.MathHelper;

import java.io.IOException;
import javax.annotation.Nonnull;

public class GuiBigSettings extends GuiScreen implements GuiResponder, FormatHelper {

    private BigPony bigPony;

    private GuiSlider xSize, ySize, zSize, height, distance;
    private GuiButton resetXSize, resetYSize, resetZSize, resetHeight, resetDistance;
    private GuiButton humanPreset, marePreset, stallionPreset, princessPreset, fillyPreset;

    public GuiBigSettings(BigPony bigPony) {
        this.bigPony = bigPony;
    }

    @Override
    public void initGui() {

        // sliders
        this.buttonList.add(xSize = new GuiSlider(this, 1, 5, 40, "X Scale", .1F, 2F, bigPony.getXScale(), this));
        this.buttonList.add(ySize = new GuiSlider(this, 2, 5, 60, "Y Scale", .1F, 2F, bigPony.getYScale(), this));
        this.buttonList.add(zSize = new GuiSlider(this, 3, 5, 80, "Z Scale", .1F, 2F, bigPony.getZScale(), this));
        this.buttonList.add(height = new GuiSlider(this, 4, 5, 100, "Eye Height", .1F, 2F, bigPony.getHeight(), this));
        this.buttonList.add(distance = new GuiSlider(this, 5, 5, 120, "Camera Distance", .1F, 2F, bigPony.getDistance(), this));

        // resets
        this.buttonList.add(resetXSize = new GuiButton(6, 160, 40, 20, 20, "*"));
        this.buttonList.add(resetYSize = new GuiButton(7, 160, 60, 20, 20, "*"));
        this.buttonList.add(resetZSize = new GuiButton(8, 160, 80, 20, 20, "*"));
        this.buttonList.add(resetHeight = new GuiButton(9, 160, 100, 20, 20, "*"));
        this.buttonList.add(resetDistance = new GuiButton(10, 160, 120, 20, 20, "*"));

        // presets
        this.buttonList.add(humanPreset = new GuiButton(11, 220, 40, 120, 20, "Human"));
        this.buttonList.add(marePreset = new GuiButton(12, 220, 60, 120, 20, "Mare"));
        this.buttonList.add(stallionPreset = new GuiButton(13, 220, 80, 120, 20, "Stallion"));
        this.buttonList.add(princessPreset = new GuiButton(14, 220, 100, 120, 20, "Princess"));
        this.buttonList.add(fillyPreset = new GuiButton(15, 220, 120, 120, 20, "Filly"));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.drawCenteredString(this.fontRenderer, "BigPony settings", width / 2, 10, -1);
        this.drawCenteredString(this.fontRenderer, "Camera Presets", 280, 25, -1);
    }

    @Override
    public void updateScreen() {

        resetXSize.enabled = !FloatUtils.equals(xSize.getSliderValue(), 1);
        resetYSize.enabled = !FloatUtils.equals(ySize.getSliderValue(), 1);
        resetZSize.enabled = !FloatUtils.equals(zSize.getSliderValue(), 1);
        resetHeight.enabled = !FloatUtils.equals(height.getSliderValue(), 1);
        resetDistance.enabled = !FloatUtils.equals(distance.getSliderValue(), 1);

        humanPreset.enabled = !CameraPresets.HUMAN.isEqual(height.getSliderValue(), distance.getSliderValue());
        marePreset.enabled = !CameraPresets.MARE.isEqual(height.getSliderValue(), distance.getSliderValue());
        stallionPreset.enabled = !CameraPresets.STALLION.isEqual(height.getSliderValue(), distance.getSliderValue());
        princessPreset.enabled = !CameraPresets.ALICORN.isEqual(height.getSliderValue(), distance.getSliderValue());
        fillyPreset.enabled = !CameraPresets.FILLY.isEqual(height.getSliderValue(), distance.getSliderValue());
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 6:
                xSize.setSliderValue(1, true);
                break;
            case 7:
                ySize.setSliderValue(1, true);
                break;
            case 8:
                zSize.setSliderValue(1, true);
                break;
            case 9:
                height.setSliderValue(1, true);
                break;
            case 10:
                distance.setSliderValue(1, true);
                break;
            case 11:
                applyPreset(CameraPresets.HUMAN);
                break;
            case 12:
                applyPreset(CameraPresets.MARE);
                break;
            case 13:
                applyPreset(CameraPresets.STALLION);
                break;
            case 14:
                applyPreset(CameraPresets.ALICORN);
                break;
            case 15:
                applyPreset(CameraPresets.FILLY);
                break;
        }
    }

    private void applyPreset(CameraPresets preset) {
        height.setSliderValue(preset.getHeight(), true);
        distance.setSliderValue(preset.getDistance(), true);
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
                bigPony.setScale(value, y, z);
                break;
            case 2:
                bigPony.setScale(x, value, z);
                break;
            case 3:
                bigPony.setScale(x, y, value);
                break;
            case 4:
                bigPony.setHeight(value);
                break;
            case 5:
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
