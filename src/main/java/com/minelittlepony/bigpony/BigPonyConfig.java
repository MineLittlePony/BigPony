package com.minelittlepony.bigpony;

import java.nio.file.Path;

import com.minelittlepony.bigpony.data.EntityScale;
import com.minelittlepony.common.util.settings.Config;
import com.minelittlepony.common.util.settings.Setting;

public class BigPonyConfig extends Config {
    public final Setting<EntityScale> scale = value("common", "scale", () -> EntityScale.DEFAULT, EntityScale.CODEC);

    public final Setting<Boolean> useDetectedPonyScaling = value("client", "useDetectedPonyScaling", false);

    public final Setting<Boolean> logNetworkEvents = value("server", "logNetworkEvents", false);
    public final Setting<Float> maxScalingMultiplier = value("server", "maxScalingMultiplier", 2F);
    public final Setting<Float> minScalingMultiplier = value("server", "minScalingMultiplier", 0.04F);
    public final Setting<Boolean> allowHitboxChanges = value("server", "allowHitboxChanges", true);
    public final Setting<Boolean> allowCameraChanges = value("server", "allowCameraChanges", true);
    public final Setting<Boolean> allowFreeformResizing = value("server", "allowFreeformResizing", true);

    public BigPonyConfig(Path path) {
        super(HEIRARCHICAL_JSON_ADAPTER, path);
    }

    public long getPermissions() {
        return Permissions.pack(allowHitboxChanges.get(), allowCameraChanges.get(), allowFreeformResizing.get());
    }
}
