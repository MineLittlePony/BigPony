package com.minelittlepony.bigpony;

import net.fabricmc.api.ModInitializer;

public class BigPony implements ModInitializer {
    @Override
    public void onInitialize() {
        Network.bootstrap();
    }
}
