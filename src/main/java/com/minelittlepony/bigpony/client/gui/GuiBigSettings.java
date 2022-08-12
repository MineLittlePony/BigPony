package com.minelittlepony.bigpony.client.gui;

import java.util.function.Function;
import java.util.stream.Stream;

import com.minelittlepony.bigpony.BigPony;
import com.minelittlepony.bigpony.Scaled;
import com.minelittlepony.bigpony.Scaling;
import com.minelittlepony.bigpony.minelittlepony.PresetDetector;
import com.minelittlepony.common.client.gui.GameGui;
import com.minelittlepony.common.client.gui.ScrollContainer;
import com.minelittlepony.common.client.gui.element.AbstractSlider;
import com.minelittlepony.common.client.gui.element.Button;
import com.minelittlepony.common.client.gui.element.Label;
import com.minelittlepony.common.client.gui.element.Toggle;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class GuiBigSettings extends GameGui {
    public static final Text TITLE = Text.translatable("minebp.options.title");
    public static final Text OPTION_DISABLED = Text.translatable("minebp.options.disabled").formatted(Formatting.YELLOW);

    private final Scaling bigPony;

    final ScrollContainer content = new ScrollContainer();

    private ResettableSlider global, xSize, ySize, zSize, height, distance;

    private CameraPresetButton[] presets;

    public GuiBigSettings(Screen parent) {
        super(TITLE);
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) {
            bigPony = BigPony.getInstance().getScaling();
        } else {
            bigPony = ((Scaled)client.player).getScaling();
        }

        content.margin.top = 30;
        content.margin.bottom = 30;
        content.getContentPadding().top = 10;
        content.getContentPadding().right = 10;
        content.getContentPadding().bottom = 20;
        content.getContentPadding().left = 10;
    }

    public boolean hasCameraConsent() {
        return client.player == null || (bigPony.hasFreeformConsent() && bigPony.hasCameraConsent());
    }

    public boolean hasScalingConsent() {
        return (client.player == null || bigPony.hasFreeformConsent()) && bigPony.isVisual();
    }

    @Override
    protected void init() {
        content.init(this::rebuildContent);
    }

    private void rebuildContent() {
        getChildElements().add(content);

        int top = 0;
        int left = width / 2 - 150;
        int right = width / 2 + 30;

        boolean allowCamera = hasCameraConsent();
        boolean allowHitbox = client.player == null || bigPony.hasHitboxConsent();
        boolean allowScaling = hasScalingConsent();

        addButton(new Label(width / 2, 6)).setCentered().getStyle().setText(getTitle().getString());

        content.addButton(new Label(left, top)).getStyle().setText("minebp.options.body");
        content.addButton(new Label(left, top + 100)).getStyle().setText("minebp.options.camera");
        content.addButton(new Label(right, top)).getStyle().setText("minebp.options.presets");

        float max = bigPony.getMaxMultiplier();

        content.addButton(global = new ResettableSlider(content, left, top += 20, .1F, max, bigPony.getScale().x))
            .onChange(value -> {
                xSize.setValue(value);
                ySize.setValue(value);
                zSize.setValue(value);
                height.setValue(value);
                distance.setValue(1 + (value - 1) / 2);
                return value;
            })
            .setEnabled(allowScaling)
            .getStyle().setText("minebp.scale.global");
        content.addButton(xSize = new ResettableSlider(content, left, top += 20, .1F, max, bigPony.getScale().x))
            .onChange(v -> {
                bigPony.getScale().x = v;
                bigPony.markDirty();
                return v;
            })
            .setTextFormat(format("minebp.scale.x"))
            .setEnabled(allowScaling);
        content.addButton(ySize = new ResettableSlider(content, left, top += 20, .1F, max, bigPony.getScale().y))
            .onChange(v -> {
                bigPony.getScale().y = v;
                bigPony.markDirty();
                return v;
            })
            .setTextFormat(format("minebp.scale.y"))
            .setEnabled(allowScaling);
        content.addButton(zSize = new ResettableSlider(content, left, top += 20, .1F, max, bigPony.getScale().z))
            .onChange(v -> {
                bigPony.getScale().z = v;
                bigPony.markDirty();
                return v;
            })
            .setTextFormat(format("minebp.scale.z"))
            .setEnabled(allowScaling);

        top += 20;

        content.addButton(height = new ResettableSlider(content, left, top += 20, .1F, max, bigPony.getCamera().height))
            .onChange(bigPony::setHeight)
            .setTextFormat(format("minebp.camera.height"))
            .setEnabled(allowCamera && allowScaling);
        content.addButton(distance = new ResettableSlider(content, left, top += 20, .1F, max, bigPony.getCamera().distance))
            .onChange(bigPony::setDistance)
            .setTextFormat(format("minebp.camera.distance"))
            .setEnabled(allowCamera && allowScaling);

        Toggle visual;
        content.addButton(visual = new Toggle(left, top += 30, !bigPony.isVisual()))
            .onChange(v -> {
                bigPony.setVisual(!v);
                if (v) {
                    visual.setEnabled(false);
                    PresetDetector.getInstance().detectPreset(client.getSession().getProfile(), bigPony).handle((skin, ex) -> {
                        visual.setEnabled(true);
                        xSize.setValue(bigPony.getScale().x);
                        ySize.setValue(bigPony.getScale().y);
                        zSize.setValue(bigPony.getScale().z);
                        height.setValue(bigPony.getCamera().height);
                        distance.setValue(bigPony.getCamera().distance);
                        tick();
                        return null;
                    });
                }
                tick();
                return v;
            })
            .getStyle().setText("minebp.camera.auto");

        if (!allowCamera || !allowHitbox) {
            content.addButton(new Label(left, top += 20)).getStyle().setText(OPTION_DISABLED);
        }

        presets = Stream.of(CameraPresets.values())
                .map(preset -> new CameraPresetButton(this, preset, right))
                .toArray(CameraPresetButton[]::new);

        addButton(new Button(width / 2 - 100, super.height - 25, 200, 20))
            .onClick(sender -> finish())
            .getStyle()
            .setText("gui.done");

        tick();
    }

    static Function<AbstractSlider<Float>, Text> format(String key) {
        return slider -> Text.translatable(key, String.format("%.2f", slider.getValue()));
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        renderBackground(stack);
        super.render(stack, mouseX, mouseY, partialTicks);
        content.render(stack, mouseX, mouseY, partialTicks);
    }

    @Override
    public void tick() {
        boolean allowCamera = hasCameraConsent();
        boolean allowScaling = hasScalingConsent();

        for (int i = 0; i < presets.length; i++) {
            presets[i].updateEnabled(height.getValue(), distance.getValue(), xSize.getValue(), ySize.getValue(), zSize.getValue());
        }

        global.setEnabled(allowScaling);
        xSize.setEnabled(allowScaling);
        ySize.setEnabled(allowScaling);
        zSize.setEnabled(allowScaling);

        height.setEnabled(allowCamera && allowScaling);
        distance.setEnabled(allowCamera && allowScaling);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void close() {
        super.close();
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
