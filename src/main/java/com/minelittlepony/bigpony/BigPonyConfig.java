package com.minelittlepony.bigpony;

import java.nio.file.Path;

import com.minelittlepony.common.util.settings.Config;
import com.minelittlepony.common.util.settings.Setting;

public class BigPonyConfig extends Config {
    public final Setting<Scaling> scale = value("scale", new Scaling(new Triple(1), new Cam(1)));

    public final Setting<Boolean> allowHitboxChanges = value("allowHitboxChanges", true);
    public final Setting<Boolean> allowCameraChanges = value("allowCameraChanges", true);
    public final Setting<Boolean> allowFreeformResizing = value("allowFreeformResizing", true);

    public BigPonyConfig(Path path) {
        super(FLATTENED_JSON_ADAPTER, path);
    }
}
