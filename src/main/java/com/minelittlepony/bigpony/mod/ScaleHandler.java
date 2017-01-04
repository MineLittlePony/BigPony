package com.minelittlepony.bigpony.mod;

import playersync.client.api.ChannelHandler;

import java.util.UUID;

/**
 * Created by Matthew on 12/13/2016.
 */
public class ScaleHandler implements ChannelHandler<IPlayerScale> {

    private PlayerSizeManager manager;

    public ScaleHandler(PlayerSizeManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(String chan, UUID uuid, IPlayerScale obj) {
        this.manager.handlePacket(uuid, obj);
    }
}
