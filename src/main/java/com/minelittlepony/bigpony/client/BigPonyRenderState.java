package com.minelittlepony.bigpony.client;

import com.minelittlepony.bigpony.Scaling;
import com.minelittlepony.bigpony.data.BodyScale;
import com.minelittlepony.bigpony.network.InteractionManager;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class BigPonyRenderState {
    public BodyScale bodyScale = BodyScale.DEFAULT;

    private final boolean isPony;

    public BigPonyRenderState(Holder holder) {
        isPony = holder instanceof EntityRenderState state && BigPonyClient.isPony(state);
    }

    public void update(LivingEntity entity, Scaling scaling) {
        this.update(entity, scaling, isPony);
    }

    public void update(Entity entity) {
        if (entity instanceof LivingEntity living && entity instanceof Scaling.Holder holder) {
            update(living, holder.getScaling(), BigPonyClient.isPony(living));
        }
    }

    private void update(LivingEntity entity, Scaling scaling, boolean isPony) {
        bodyScale = isPony ? scaling.getDimensions().model() : scaling.getDimensions().body();
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
