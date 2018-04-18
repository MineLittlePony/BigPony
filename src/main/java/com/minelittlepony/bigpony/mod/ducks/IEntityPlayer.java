package com.minelittlepony.bigpony.mod.ducks;

import com.minelittlepony.bigpony.mod.IPlayerScale;

public interface IEntityPlayer {

    IPlayerScale getPlayerScale();

    void setPlayerScale(IPlayerScale scale);

    void updatePlayerScale(IEntityPlayer old);
}
