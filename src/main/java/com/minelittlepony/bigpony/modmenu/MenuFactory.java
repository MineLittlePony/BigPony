package com.minelittlepony.bigpony.modmenu;

import com.minelittlepony.bigpony.client.gui.GuiBigSettings;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class MenuFactory implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return GuiBigSettings::new;
    }
}
