package com.minelittlepony.bigpony;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.minelittlepony.bigpony.network.Network;
import com.minelittlepony.common.util.GamePaths;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

public class BigPony implements ModInitializer {

    public static final Logger LOGGER = LogManager.getLogger("BigPony");

    private static BigPony instance;

    public static BigPony getInstance() {
        return instance;
    }

    public static Identifier id(String name) {
        return Identifier.of("minebp", name);
    }

    private final BigPonyConfig config = new BigPonyConfig(GamePaths.getConfigDirectory().resolve("bigpony.json"));

    public BigPony() {
        instance = this;
    }

    public BigPonyConfig getConfig() {
        return config;
    }

    @Override
    public void onInitialize() {
        config.load();
        Network.bootstrap();
    }
}
