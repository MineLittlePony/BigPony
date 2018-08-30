package com.minelittlepony.pony.data;

import net.minecraft.client.entity.AbstractClientPlayer;

/**
 * Stub
 */
public interface IPony {
    static IPony forPlayer(AbstractClientPlayer player) {
        return null;
    }

    PonyRace getRace(boolean ignorePony);

    IPonyData getMetadata();
}
