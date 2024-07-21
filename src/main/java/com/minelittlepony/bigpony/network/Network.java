package com.minelittlepony.bigpony.network;

import com.minelittlepony.bigpony.BigPony;
import com.minelittlepony.bigpony.Scaling;
import com.sollace.fabwork.api.packets.C2SPacketType;
import com.sollace.fabwork.api.packets.S2CPacketType;
import com.sollace.fabwork.api.packets.SimpleNetworking;

import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

public class Network {
    public static final S2CPacketType<ConsentPacket> SERVER_CONSENT = SimpleNetworking.serverToClient(BigPony.id("consent"), ConsentPacket::new);
    public static final C2SPacketType<MsgPlayerSize> PLAYER_SIZE = SimpleNetworking.clientToServer(BigPony.id("player_size"), MsgPlayerSize::new);
    public static final S2CPacketType<MsgPlayerSize> OTHER_PLAYER_SIZE = SimpleNetworking.serverToClient(BigPony.id("other_player_size"), MsgPlayerSize::new);

    public static void bootstrap() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            BigPony.LOGGER.info("Sending consent packet to " + handler.getPlayer().getName().getString());
            sender.sendPacket(SERVER_CONSENT.toPacket(new ConsentPacket()));
        });
        ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register((player, origin, destination) -> {
            BigPony.LOGGER.info("Sending consent packet to " + player.getName().getString());
            SERVER_CONSENT.sendToPlayer(new ConsentPacket(), player);
        });
        ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) -> {
            Scaling newScaling = ((Scaling.Holder)newPlayer).getScaling();
            newScaling.setDimensions(((Scaling.Holder)oldPlayer).getScaling().getDimensions());
            newScaling.markDirty();
        });

        PLAYER_SIZE.receiver().addPersistentListener((player, packet) -> {
            ((Scaling.Holder)player).getScaling().setDimensions(packet.dimensions());
        });
    }
}
