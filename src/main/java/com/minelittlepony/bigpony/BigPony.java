package com.minelittlepony.bigpony;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.minelittlepony.common.util.GamePaths;

import net.fabricmc.api.ModInitializer;

public class BigPony implements ModInitializer {

    public static final Logger LOGGER = LogManager.getLogger("BigPony");

    private static BigPony instance;

    public static BigPony getInstance() {
        return instance;
    }

    private final Config config = new Config(GamePaths.getConfigDirectory().resolve("bigpony.json"));

    public BigPony() {
        instance = this;
    }

    public Config getConfig() {
        return config;
    }

    public Scaling getScaling() {
        return config.scale.get();
    }

    @Override
    public void onInitialize() {
        config.load();
        Network.bootstrap();
    }
}
