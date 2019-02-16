package com.minelittlepony.bigpony.mod.gui;

import com.mumfrey.liteloader.client.gui.GuiCheckbox;

import java.util.function.Consumer;

import net.minecraft.client.resources.I18n;

public class Checkbox extends GuiCheckbox implements IPerformable {

    private final Consumer<Boolean> action;

    public Checkbox(int x, int y, String displayString, boolean value, Consumer<Boolean> callback) {
        super(0, x, y, I18n.format(displayString));
        action = callback;
        checked = value;
    }

    @Override
    public void performAction() {
        checked = !checked;
        action.accept(checked);
    }
}
