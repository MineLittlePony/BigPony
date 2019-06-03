package com.minelittlepony.bigpony.ducks;

import com.minelittlepony.bigpony.scale.IPlayerScale;

public interface IEntityPlayer {

    IPlayerScale getPlayerScale();

    void setPlayerScale(IPlayerScale scale);
}
