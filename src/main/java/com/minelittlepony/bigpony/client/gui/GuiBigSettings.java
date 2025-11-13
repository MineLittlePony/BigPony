package com.minelittlepony.bigpony.client.gui;

import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

import com.minelittlepony.bigpony.BigPony;
import com.minelittlepony.bigpony.Permissions;
import com.minelittlepony.bigpony.Scaling;
import com.minelittlepony.bigpony.data.EntityScale;
import com.minelittlepony.bigpony.minelittlepony.PresetDetector;
import com.minelittlepony.bigpony.network.InteractionManager;
import com.minelittlepony.common.client.gui.GameGui;
import com.minelittlepony.common.client.gui.ScrollContainer;
import com.minelittlepony.common.client.gui.element.AbstractSlider;
import com.minelittlepony.common.client.gui.element.Button;
import com.minelittlepony.common.client.gui.element.Label;
import com.minelittlepony.common.client.gui.element.Toggle;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class GuiBigSettings extends GameGui {
    public static final Text TITLE = Text.translatable("minebp.options.title");
    public static final Text OPTION_DISABLED = Text.translatable("minebp.options.disabled").formatted(Formatting.YELLOW);

    private EntityScale dimensions;
    private EntityScale initialDimensions;
    private boolean initialDetectorState;

    final ScrollContainer content = new ScrollContainer();

    private Button revert;
    private ResettableSlider global, xSize, ySize, zSize, height, distance;

    private CameraPresetButton[] presets;

    public GuiBigSettings(Screen parent) {
        super(TITLE);
        content.margin.top = 30;
        content.margin.bottom = 30;
        content.getContentPadding().top = 10;
        content.getContentPadding().right = 10;
        content.getContentPadding().bottom = 20;
        content.getContentPadding().left = 10;
        loadDimensions();
    }

    public boolean hasCameraConsent() {
        return client.player == null || Permissions.camera(InteractionManager.getInstance().getPermissions());
    }

    public boolean hasScalingConsent() {
        return client.player == null || Permissions.freeform(InteractionManager.getInstance().getPermissions());
    }

    public boolean isScalingButtonsEnabled() {
        return (client.player == null || Permissions.freeform(InteractionManager.getInstance().getPermissions())) && !BigPony.getInstance().getConfig().useDetectedPonyScaling.get();
    }

    public boolean hasHitboxConsent() {
        return client.player == null || Permissions.hitbox(InteractionManager.getInstance().getPermissions());
    }

    @Override
    protected void init() {
        addButton(new Label(width / 2, 6)).setCentered().getStyle().setText(getTitle().getString());

        content.init(this::rebuildContent);

        addButton(new Button(width / 2 - 110, super.height - 25, 100, 20))
            .onClick(sender -> finish())
            .getStyle()
            .setText("gui.done");
        addButton(revert = new Button(width / 2 + 10, super.height - 25, 100, 20))
            .onClick(sender -> {
                dimensions = initialDimensions;
                BigPony.getInstance().getConfig().useDetectedPonyScaling.set(initialDetectorState);
                if (initialDetectorState) {
                    PresetDetector.getInstance().detectPreset(client.getGameProfile());
                } else {
                    PresetDetector.getInstance().revertFillyCam();
                }
                BigPony.getInstance().getConfig().save();
                updateDimensions();
                clearAndInit();
            })
            .getStyle()
            .setText("controls.reset");
        tick();
    }

    private void loadDimensions() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) {
            dimensions = BigPony.getInstance().getConfig().scale.get();
        } else {
            dimensions = ((Scaling.Holder)client.player).getScaling().getDimensions();
        }
        initialDimensions = dimensions;
        initialDetectorState = BigPony.getInstance().getConfig().useDetectedPonyScaling.get();
    }

    private void rebuildContent() {
        getChildElements().add(content);

        int top = 0;
        int left = width / 2 - 150;
        int right = width / 2 + 30;

        boolean allowCamera = hasCameraConsent();
        boolean allowHitbox = hasHitboxConsent();
        boolean allowScaling = isScalingButtonsEnabled();

        content.addButton(new Label(left, top)).getStyle().setText("minebp.options.body");
        content.addButton(new Label(left, top + 100)).getStyle().setText("minebp.options.camera");
        content.addButton(new Label(right, top)).getStyle().setText("minebp.options.presets");

        float max = InteractionManager.getInstance().getMaxMultiplier();

        content.addButton(global = new ResettableSlider(content, left, top += 20, .1F, max, dimensions.body().x()))
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
        content.addButton(xSize = new ResettableSlider(content, left, top += 20, .1F, max, dimensions.body().x()))
            .onChange(v -> {
                dimensions = dimensions.withModel(dimensions.model().withX(v));
                updateDimensions();
                return v;
            })
            .setTextFormat(format("minebp.scale.x"))
            .setEnabled(allowScaling);
        content.addButton(ySize = new ResettableSlider(content, left, top += 20, .1F, max, dimensions.body().y()))
            .onChange(v -> {
                dimensions = dimensions.withModel(dimensions.model().withY(v));
                updateDimensions();
                return v;
            })
            .setTextFormat(format("minebp.scale.y"))
            .setEnabled(allowScaling);
        content.addButton(zSize = new ResettableSlider(content, left, top += 20, .1F, max, dimensions.body().z()))
            .onChange(v -> {
                dimensions = dimensions.withModel(dimensions.model().withZ(v));
                updateDimensions();
                return v;
            })
            .setTextFormat(format("minebp.scale.z"))
            .setEnabled(allowScaling);

        top += 20;

        content.addButton(height = new ResettableSlider(content, left, top += 20, .1F, max, dimensions.camera().height()))
            .onChange(v -> {
                dimensions = dimensions.withCamera(dimensions.camera().withHeight(v));
                updateDimensions();
                return v;
            })
            .setTextFormat(format("minebp.camera.height"))
            .setEnabled(allowCamera && allowScaling);
        content.addButton(distance = new ResettableSlider(content, left, top += 20, .1F, max, dimensions.camera().distance()))
            .onChange(v -> {
                dimensions = dimensions.withCamera(dimensions.camera().withDistance(v));
                updateDimensions();
                return v;
            })
            .setTextFormat(format("minebp.camera.distance"))
            .setEnabled(allowCamera && allowScaling);

        Toggle visual;
        content.addButton(visual = new Toggle(left, top += 30, BigPony.getInstance().getConfig().useDetectedPonyScaling.get())).onChange(v -> {
                BigPony.getInstance().getConfig().useDetectedPonyScaling.set(v);
                BigPony.getInstance().getConfig().save();
                if (v) {
                    visual.setEnabled(false);
                    PresetDetector.getInstance().detectPreset(client.getGameProfile()).thenAccept(dimensions -> {
                        xSize.setValue(dimensions.body().x());
                        ySize.setValue(dimensions.body().y());
                        zSize.setValue(dimensions.body().z());
                        height.setValue(dimensions.camera().height());
                        distance.setValue(dimensions.camera().distance());
                        this.dimensions = dimensions;
                        updateDimensions();
                        tick();
                        visual.setEnabled(true);
                    });
                } else {
                    toggleMLPScalingOff();
                }
                tick();
                return v;
            })
            .getStyle().setText("minebp.camera.auto");

        if (!allowCamera || !allowHitbox || !hasScalingConsent()) {
            content.addButton(new Label(left, top += 20)).getStyle().setText(OPTION_DISABLED);
        }

        presets = Stream.of(CameraPresets.values())
                .map(preset -> new CameraPresetButton(this, preset, right))
                .toArray(CameraPresetButton[]::new);
    }

    public void toggleMLPScalingOff() {
        PresetDetector.getInstance().revertFillyCam();
        dimensions = hasScalingConsent() ? dimensions.withModel(dimensions.body()) : EntityScale.DEFAULT;
        updateDimensions();
    }

    static Function<AbstractSlider<Float>, Text> format(String key) {
        return slider -> Text.translatable(key, String.format("%.2f", slider.getValue()));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float partialTicks) {
        super.render(context, mouseX, mouseY, partialTicks);
        content.render(context, mouseX, mouseY, partialTicks);
    }

    @Override
    public void tick() {
        boolean allowCamera = hasCameraConsent();
        boolean allowScaling = isScalingButtonsEnabled();

        for (int i = 0; i < presets.length; i++) {
            presets[i].updateEnabled(height.getValue(), distance.getValue(), xSize.getValue(), ySize.getValue(), zSize.getValue());
        }

        global.setEnabled(allowScaling);
        xSize.setEnabled(allowScaling);
        ySize.setEnabled(allowScaling);
        zSize.setEnabled(allowScaling);

        height.setEnabled(allowCamera && allowScaling);
        distance.setEnabled(allowCamera && allowScaling);
        revert.setEnabled(!Objects.equals(dimensions, initialDimensions) || (initialDetectorState != BigPony.getInstance().getConfig().useDetectedPonyScaling.get()));
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    private void updateDimensions() {
        if (client.player instanceof Scaling.Holder holder) {
            holder.getScaling().setDimensions(dimensions);
        }
    }

    @Override
    public void close() {
        super.close();
        BigPony.getInstance().getConfig().scale.set(dimensions);
        BigPony.getInstance().getConfig().save();
        updateDimensions();
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
