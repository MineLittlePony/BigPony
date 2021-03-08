package com.minelittlepony.bigpony;

import java.nio.file.Path;

import com.minelittlepony.common.util.settings.JsonConfig;
import com.minelittlepony.common.util.settings.Setting;

public class Config extends JsonConfig {

    public final Setting<Scaling> scale = value("scale", new Scaling(new Triple(1), new Cam(1)));

    public final Setting<Boolean> allowHitboxChanges = value("allowHitboxChanges", true);
    public final Setting<Boolean> allowCameraChanges = value("allowCameraChanges", true);

    public Config(Path path) {
        super(path);
    }
}
