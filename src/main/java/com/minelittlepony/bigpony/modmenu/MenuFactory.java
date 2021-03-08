package com.minelittlepony.bigpony.modmenu;

import com.minelittlepony.bigpony.client.gui.GuiBigSettings;

import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;

public class MenuFactory implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return GuiBigSettings::new;
    }
}
