package com.minelittlepony.bigpony;

import java.util.UUID;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;

/**
 * Received on the server to notify us when a client user changes their size.
 */
public class MsgPlayerSize implements Network.Packet {

    protected final UUID playerId;

    protected final Scaling scaling;

    protected final boolean force;

    protected final boolean consentHitboxes;
    protected final boolean consentCamera;
    protected final boolean consentFreeform;

    public MsgPlayerSize(UUID sender, Scaling scaling, boolean force) {
        playerId = sender;
        this.scaling = scaling;
        this.force = force;
        consentHitboxes = BigPony.getInstance().getConfig().allowHitboxChanges.get();
        consentCamera = BigPony.getInstance().getConfig().allowCameraChanges.get();
        consentFreeform = BigPony.getInstance().getConfig().allowFreeformResizing.get();
    }

    public MsgPlayerSize(PacketByteBuf buff) {
        playerId = buff.readUuid();
        scaling = new Scaling(
                new Triple(buff.readFloat(), buff.readFloat(), buff.readFloat()),
                new Cam(buff.readFloat(),  buff.readFloat())
        );
        scaling.maxMultiplier = buff.readFloat();
        scaling.setVisual(buff.readBoolean());
        force = buff.readBoolean();
        consentHitboxes = buff.readBoolean();
        consentCamera = buff.readBoolean();
        consentFreeform = buff.readBoolean();
    }

    @Override
    public PacketByteBuf toBuffer() {
        PacketByteBuf buff = PacketByteBufs.create();
        buff.writeUuid(playerId);
        buff.writeFloat(scaling.getScale().x);
        buff.writeFloat(scaling.getScale().y);
        buff.writeFloat(scaling.getScale().z);
        buff.writeFloat(scaling.getCamera().distance);
        buff.writeFloat(scaling.getCamera().height);
        buff.writeFloat(scaling.getMaxMultiplier());
        buff.writeBoolean(scaling.isVisual());
        buff.writeBoolean(force);
        buff.writeBoolean(consentHitboxes);
        buff.writeBoolean(consentCamera);
        buff.writeBoolean(consentFreeform);
        return buff;
    }

    @Override
    public void handle(PlayerEntity sender) {
        Scaling sc = ((Scaled)sender).getScaling();
        if (force || !sc.isConfigured()) {
            sc.initFrom(scaling);
        }
        sc.updateConsent(consentCamera, consentHitboxes, consentFreeform, Math.min(
                scaling.getMaxMultiplier(),
                BigPony.getInstance().getScaling().getMaxMultiplier()
        ));
        sc.markDirty();
    }
}
