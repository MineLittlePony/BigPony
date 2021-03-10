package com.minelittlepony.bigpony;

import java.util.UUID;

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
    protected final boolean consentcamera;

    public MsgPlayerSize(UUID sender, Scaling scaling, boolean force) {
        playerId = sender;
        this.scaling = scaling;
        this.force = force;
        consentHitboxes = BigPony.getInstance().getConfig().allowHitboxChanges.get();
        consentcamera = BigPony.getInstance().getConfig().allowCameraChanges.get();
    }

    public MsgPlayerSize(PacketByteBuf buff) {
        playerId = buff.readUuid();
        scaling = new Scaling(
                new Triple(buff.readFloat(), buff.readFloat(), buff.readFloat()),
                new Cam(buff.readFloat(),  buff.readFloat())
        );
        force = buff.readBoolean();
        consentHitboxes = buff.readBoolean();
        consentcamera = buff.readBoolean();
    }

    @Override
    public void toBuffer(PacketByteBuf buff) {
        buff.writeUuid(playerId);
        buff.writeFloat(scaling.getScale().x);
        buff.writeFloat(scaling.getScale().y);
        buff.writeFloat(scaling.getScale().z);
        buff.writeFloat(scaling.getCamera().distance);
        buff.writeFloat(scaling.getCamera().height);
        buff.writeBoolean(force);
        buff.writeBoolean(consentHitboxes);
        buff.writeBoolean(consentcamera);
    }

    @Override
    public void handle(PlayerEntity sender) {
        BigPony.LOGGER.info("[SERVER] Got size for " + sender.getName().asString());

        Scaling sc = ((Scaled)sender).getScaling();
        if (force || !sc.isConfigured()) {
            sc.initFrom(scaling);
        }
        sc.markDirty();
        sc.updateConsent(consentcamera, consentHitboxes);
    }
}
