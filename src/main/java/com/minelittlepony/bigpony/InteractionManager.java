package com.minelittlepony.bigpony;

public class InteractionManager {
    private static InteractionManager INSTANCE = new InteractionManager();

    protected float maxMultiplier = 2;

    public static InteractionManager getInstance() {
        return INSTANCE;
    }

    protected InteractionManager() {
        INSTANCE = this;
    }

    public long getPermissions() {
        return BigPony.getInstance().getConfig().getPermissions();
    }

    public float getMaxMultiplier() {
        return BigPony.getInstance().getConfig().maxScalingMultiplier.get();
    }

    public long getLastSettingsUpdateTime() {
        return 0;
    }
}
