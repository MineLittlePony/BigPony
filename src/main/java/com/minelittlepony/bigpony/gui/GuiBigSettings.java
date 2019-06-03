package com.minelittlepony.bigpony.gui;

import com.minelittlepony.bigpony.scale.IPlayerScale;
import com.minelittlepony.common.client.gui.GameGui;
import com.minelittlepony.common.client.gui.element.Label;

import net.minecraft.network.chat.TranslatableComponent;

public class GuiBigSettings extends GameGui {

    private final IPlayerScale bigPony;

    private ResettableSlider xSize, ySize, zSize, height, distance;

    private CameraPresetButton[] presets;

    public GuiBigSettings(IPlayerScale bigPony) {
        super(new TranslatableComponent("minebp.options.title"));
        this.bigPony = bigPony;
    }

    @Override
    protected void init() {
        int top = 20;
        int left = width / 2 - 200;
        int right = width / 2 + 50;

        addButton(new Label(width / 2, 6)).setCentered().getStyle().setText(getTitle().getString());
        addButton(new Label(left + 80, 20)).getStyle().setText("minebp.options.body");
        addButton(new Label(left + 80, 120)).getStyle().setText("minebp.options.camera");
        addButton(new Label((width / 2 + 16) + 90, 20)).getStyle().setText("minebp.options.presets");

        addButton(new ResettableSlider(this, left, top += 20, .1F, 2F, bigPony.getXScale()))
            .onChange(value -> {
                xSize.setValue(value);
                ySize.setValue(value);
                zSize.setValue(value);
                height.setValue(value);
                return value;
            })
            .getStyle().setText("minebp.scale.global");
        addButton(xSize = new ResettableSlider(this, left, top += 20, .1F, 2F, bigPony.getXScale()))
            .onChange(bigPony::setXScale)
            .getStyle().setText("minebp.scale.x");
        addButton(ySize = new ResettableSlider(this, left, top += 20, .1F, 2F, bigPony.getYScale()))
            .onChange(bigPony::setYScale)
            .getStyle().setText("minebp.scale.y");
        addButton(zSize = new ResettableSlider(this, left, top += 20, .1F, 2F, bigPony.getZScale()))
            .onChange(bigPony::setZScale)
            .getStyle().setText("minebp.scale.z");

        top += 20;

        addButton(height = new ResettableSlider(this, left, top += 20, .1F, 2F, bigPony.getHeight()))
            .onChange(bigPony::setHeight)
            .getStyle().setText("minebp.camera.height");
        addButton(distance = new ResettableSlider(this, left, top += 20, .1F, 2F, bigPony.getDistance()))
            .onChange(bigPony::setDistance)
            .getStyle().setText("minebp.camera.distance");

        top += 20;

        CameraPresets[] values = CameraPresets.values();

        presets = new CameraPresetButton[values.length];

        for (int i = 0; i < presets.length; i++) {
            presets[i] = new CameraPresetButton(this, values[i], right);
        }

        tick();
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.renderBackground();
        super.render(mouseX, mouseY, partialTicks);
    }

    @Override
    public void tick() {
        for (int i = 0; i < presets.length; i++) {
            presets[i].updateEnabled(height.getValue(), distance.getValue(), xSize.getValue(), ySize.getValue(), zSize.getValue());
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public void applyPreset(CameraPresets preset, boolean camera, boolean body) {
        float h = preset.getHeight();
        if (body) {
            xSize.setValue(h);
            ySize.setValue(h);
            zSize.setValue(h);
        }
        if (camera) {
            height.setValue(h);
            distance.setValue(preset.getDistance());
        }
    }
}
