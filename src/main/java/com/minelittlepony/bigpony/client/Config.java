package com.minelittlepony.bigpony.client;

import java.nio.file.Path;

import com.minelittlepony.bigpony.Scaling;
import com.minelittlepony.bigpony.Triple;
import com.minelittlepony.common.util.settings.JsonConfig;
import com.minelittlepony.common.util.settings.Setting;

public class Config extends JsonConfig {

    public final Setting<ClientPlayerScale> scale = value("scale", new ClientPlayerScale());

    public Config(Path path) {
        super(path);
    }

    static class ClientPlayerScale extends Scaling {

        public ClientPlayerScale() {
            super(new Triple(1), 1, 1);
        }

        @Override
        public void markDirty() {
            super.markDirty();
        }
    }
}
