package com.minelittlepony.bigpony.network;

import com.minelittlepony.bigpony.BigPony;
import com.minelittlepony.bigpony.BigPonyCommand;
import com.minelittlepony.bigpony.Scaling;
import com.sollace.fabwork.api.packets.C2SPacketType;
import com.sollace.fabwork.api.packets.S2CPacketType;
import com.sollace.fabwork.api.packets.SimpleNetworking;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityLevelChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

public class Network {
    public static final S2CPacketType<ConsentPacket> SERVER_CONSENT = SimpleNetworking.serverToClient(BigPony.id("consent"), ConsentPacket.PACKET_CODEC);
    public static final C2SPacketType<MsgPlayerSize> PLAYER_SIZE = SimpleNetworking.clientToServer(BigPony.id("player_size"), MsgPlayerSize.PACKET_CODEC);
    public static final S2CPacketType<MsgPlayerSize> OTHER_PLAYER_SIZE = SimpleNetworking.serverToClient(BigPony.id("other_player_size"), MsgPlayerSize.PACKET_CODEC);

    public static void bootstrap() {
        ServerLifecycleEvents.SERVER_STARTING.register(s -> InteractionManager.getInstance().setServer(s));
        ServerPlayConnectionEvents.JOIN.register((handler, sender, _) -> {
            InteractionManager.getInstance().log("[S-JOIN] Sending settings update packet to " + handler.getPlayer().getName().getString());
            sender.sendPacket(Network.SERVER_CONSENT.toPacket(new ConsentPacket()));
        });
        ServerEntityLevelChangeEvents.AFTER_PLAYER_CHANGE_LEVEL.register((player, _, _) -> {
            InteractionManager.getInstance().log("[S-CNGWLD] Re-Sending settings update packet to " + player.getName().getString());
            Network.SERVER_CONSENT.sendToPlayer(new ConsentPacket(), player);
        });
        ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, _) -> {
            Scaling newScaling = ((Scaling.Holder)newPlayer).getScaling();
            newScaling.setDimensions(((Scaling.Holder)oldPlayer).getScaling().getDimensions());
            newScaling.markDirty();
        });
        CommandRegistrationCallback.EVENT.register((dispatcher, _, _) -> {
            dispatcher.register(BigPonyCommand.create());
        });

        PLAYER_SIZE.receiver().addPersistentListener((player, packet) -> {
            InteractionManager.getInstance().log("[S] Got size packet for client player " + player.getName().getString());
            ((Scaling.Holder)player).getScaling().setDimensions(packet.dimensions());
        });

        BigPony.getInstance().getConfig().onChangedExternally(_ -> {
            InteractionManager.getInstance().onConfigurationChange();
        });
    }
}
