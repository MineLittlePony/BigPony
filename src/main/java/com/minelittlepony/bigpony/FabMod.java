package com.minelittlepony.bigpony;

import com.minelittlepony.common.client.IModUtilities;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;

/**
 * Ah' am a big pony!
 */
public class FabMod implements ClientModInitializer, IModUtilities {
    @Override
    public void onInitializeClient() {
        BigPony bp = new BigPony(this);
        ClientTickCallback.EVENT.register(bp::onClientTick);
    }
}
