package com.minelittlepony.bigpony.client.gui;

import com.minelittlepony.bigpony.BigPony;
import com.minelittlepony.bigpony.Scaled;
import com.minelittlepony.bigpony.Scaling;
import com.minelittlepony.common.client.gui.GameGui;
import com.minelittlepony.common.client.gui.element.Button;
import com.minelittlepony.common.client.gui.element.Label;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public class GuiBigSettings extends GameGui {

    private final Scaling bigPony;

    private ResettableSlider xSize, ySize, zSize, height, distance;

    private CameraPresetButton[] presets;

    public GuiBigSettings(Screen parent) {
        super(new TranslatableText("minebp.options.title"));
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) {
            bigPony = BigPony.getInstance().getScaling();
        } else {
            bigPony = ((Scaled)client.player).getScaling();
        }
    }

    public boolean hasCameraConsent() {
        return client.player == null || bigPony.hasCameraConsent();
    }

    @Override
    protected void init() {
        int top = super.height / 8;
        int left = width / 2 - 200;
        int right = width / 2 + 60;

        boolean allowCamera = hasCameraConsent();
        boolean allowHitbox = client.player == null || bigPony.hasHitboxConsent();

        addButton(new Label(width / 2, 6)).setCentered().getStyle().setText(getTitle().getString());
        addButton(new Label(left + 95, top)).setCentered().getStyle().setText("minebp.options.body");
        addButton(new Label(left + 95, top + 100)).setCentered().getStyle().setText("minebp.options.camera");
        addButton(new Label(right + 40, top)).setCentered().getStyle().setText("minebp.options.presets");

        addButton(new ResettableSlider(this, left, top += 20, .1F, 2F, bigPony.getScale().x))
            .onChange(value -> {
                xSize.setValue(value);
                ySize.setValue(value);
                zSize.setValue(value);
                height.setValue(value);
                distance.setValue(1 + (value - 1) / 2);
                return value;
            })
            .getStyle().setText("minebp.scale.global");
        addButton(xSize = new ResettableSlider(this, left, top += 20, .1F, 2F, bigPony.getScale().x))
            .onChange(v -> {
                bigPony.getScale().x = v;
                bigPony.markDirty();
                return v;
            })
            .getStyle().setText("minebp.scale.x");
        addButton(ySize = new ResettableSlider(this, left, top += 20, .1F, 2F, bigPony.getScale().y))
            .onChange(v -> {
                bigPony.getScale().y = v;
                bigPony.markDirty();
                return v;
            })
            .getStyle().setText("minebp.scale.y");
        addButton(zSize = new ResettableSlider(this, left, top += 20, .1F, 2F, bigPony.getScale().z))
            .onChange(v -> {
                bigPony.getScale().z = v;
                bigPony.markDirty();
                return v;
            })
            .getStyle().setText("minebp.scale.z");

        top += 20;

        addButton(height = new ResettableSlider(this, left, top += 20, .1F, 2F, bigPony.getCamera().height))
            .onChange(bigPony::setHeight)
            .setEnabled(allowCamera)
            .getStyle().setText("minebp.camera.height");
        addButton(distance = new ResettableSlider(this, left, top += 20, .1F, 2F, bigPony.getCamera().distance))
            .onChange(bigPony::setDistance)
            .setEnabled(allowCamera)
            .getStyle().setText("minebp.camera.distance");

        if (!allowCamera || !allowHitbox) {
            addButton(new Label(left, top += 20)).getStyle().setText(new TranslatableText("minebp.options.disabled").formatted(Formatting.YELLOW));
        }

        top += 20;

        CameraPresets[] values = CameraPresets.values();

        presets = new CameraPresetButton[values.length];

        for (int i = 0; i < presets.length; i++) {
            presets[i] = new CameraPresetButton(this, values[i], right);
        }

        addButton(new Button(width / 2 - 50, super.height - 25, 100, 20))
            .onClick(sender -> finish())
            .getStyle()
            .setText("gui.done");

        tick();
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        renderBackground(stack);
        super.render(stack, mouseX, mouseY, partialTicks);
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

    @Override
    public void onClose() {
        super.onClose();
        BigPony.getInstance().getScaling().copyFrom(bigPony);
        BigPony.getInstance().getConfig().save();
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
