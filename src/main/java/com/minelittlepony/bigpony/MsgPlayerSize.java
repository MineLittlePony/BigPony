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

    public MsgPlayerSize(UUID sender, Scaling scaling, boolean force) {
        playerId = sender;
        this.scaling = scaling;
        this.force = force;
    }

    public MsgPlayerSize(PacketByteBuf buff) {
        playerId = buff.readUuid();
        scaling = new Scaling(new Triple(buff.readFloat(), buff.readFloat(), buff.readFloat()), buff.readFloat(),  buff.readFloat());
        force = buff.readBoolean();
    }

    @Override
    public void toBuffer(PacketByteBuf buff) {
        buff.writeUuid(playerId);
        buff.writeFloat(scaling.getScale().x);
        buff.writeFloat(scaling.getScale().y);
        buff.writeFloat(scaling.getScale().z);
        buff.writeFloat(scaling.getHeight());
        buff.writeFloat(scaling.getDistance());
        buff.writeBoolean(force);
    }

    @Override
    public void handle(PlayerEntity sender) {
        System.out.println("[SERVER] Got size for " + sender.getName().asString());

        Scaling sc = ((Scaled)sender).getScaling();
        if (force || !sc.isConfigured()) {
            sc.initFrom(scaling);
        }
        sc.markDirty();
    }
}
