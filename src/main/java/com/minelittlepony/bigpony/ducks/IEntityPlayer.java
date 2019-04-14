package com.minelittlepony.bigpony.ducks;

import com.minelittlepony.bigpony.IPlayerScale;

public interface IEntityPlayer {

    IPlayerScale getPlayerScale();

    void setPlayerScale(IPlayerScale scale);

    void updatePlayerScale(IEntityPlayer old);
}
