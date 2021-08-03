package com.minelittlepony.bigpony;

import java.util.function.Function;

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class Network {
    private static final Identifier CLIENT_UPDATE_PLAYER_SIZE_ID = new Identifier("minebp", "client_player_size");
    private static final Identifier SERVER_OTHER_PLAYER_SIZE_ID = new Identifier("minebp", "server_other_player_size");
    private static final Identifier CONSENT_ID = new Identifier("minebp", "consent");

    private static boolean registered;

    static void bootstrap() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            BigPony.LOGGER.info("Sending consent packet to " + handler.getPlayer().getName().getString());
            sender.sendPacket(CONSENT_ID, PacketByteBufs.empty());
        });
        register(CLIENT_UPDATE_PLAYER_SIZE_ID, MsgPlayerSize::new);

        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            ClientProxy.bootstrap();
        }
    }

    public static void sendPlayerSizeToClient(World world, MsgOtherPlayerSize msg) {
        world.getPlayers().forEach(player -> {
            if (player != null) {
                ServerPlayNetworking.send((ServerPlayerEntity)player, SERVER_OTHER_PLAYER_SIZE_ID, msg.toBuffer());
            }
        });
    }

    public static void sendPlayerSizeToServer(MsgPlayerSize msg) {
        if (registered) {
            if (FabricLoader.getInstance().getEnvironmentType() != EnvType.CLIENT) {
                throw new RuntimeException("Client packet send called by the server");
            }
            ClientPlayNetworking.send(CLIENT_UPDATE_PLAYER_SIZE_ID, msg.toBuffer());
        }
    }

    private static <T extends Packet> void register(Identifier id, Function<PacketByteBuf, T> factory) {
        ServerPlayNetworking.registerGlobalReceiver(id, (server, player, ignored, buffer, ignored2) -> {
            T packet = factory.apply(buffer);
            server.execute(() -> packet.handle(player));
        });
    }

    interface Packet {
        PacketByteBuf toBuffer();

        void handle(PlayerEntity sender);
    }

    private static class ClientProxy {
        static void bootstrap() {
            ClientLoginConnectionEvents.INIT.register((handler, client) -> {
                registered = false;
                BigPony.LOGGER.info("Resetting registered flag");
            });
            ClientPlayNetworking.registerGlobalReceiver(CONSENT_ID, (client, ignore1, buffer, ignore2) -> {
                registered = true;
            });
            register(SERVER_OTHER_PLAYER_SIZE_ID, MsgOtherPlayerSize::new);
        }

        static <T extends Packet> void register(Identifier id, Function<PacketByteBuf, T> factory) {
            ClientPlayNetworking.registerGlobalReceiver(id, (client, ignore1, buffer, ignore2) -> {
                T packet = factory.apply(buffer);
                client.execute(() -> packet.handle(client.player));
            });
        }
    }
}
