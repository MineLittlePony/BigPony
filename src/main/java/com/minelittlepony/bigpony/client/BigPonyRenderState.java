package com.minelittlepony.bigpony.client;

import com.minelittlepony.bigpony.Scaling;
import com.minelittlepony.bigpony.data.BodyScale;
import com.minelittlepony.bigpony.network.InteractionManager;

import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.entity.LivingEntity;

public class BigPonyRenderState {
    public BodyScale bodyScale = BodyScale.DEFAULT;

    private final boolean isPony;

    public BigPonyRenderState(EntityRenderState state) {
        isPony = BigPonyClient.isPony(state);
    }

    public void update(LivingEntity entity, Scaling scaling) {
        bodyScale = (scaling.getDimensions().visual() || !isPony) ? scaling.getDimensions().body() : BodyScale.DEFAULT;
        bodyScale = new BodyScale(
                InteractionManager.getInstance().getClamped(bodyScale.x()),
                InteractionManager.getInstance().getClamped(bodyScale.y()),
                InteractionManager.getInstance().getClamped(bodyScale.z())
        );
    }

    public interface Holder {
        BigPonyRenderState getBigPonyState();
    }
}
