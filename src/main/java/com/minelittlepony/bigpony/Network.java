package com.minelittlepony.bigpony;

import java.util.function.Function;

import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public interface Network {

    SPacketType<MsgPlayerSize> CLIENT_UPDATE_PLAYER_SIZE = clientToServer(new Identifier("minebp", "client_player_size"), MsgPlayerSize::new);

    MPacketType<MsgOtherPlayerSize> SERVER_OTHER_PLAYER_SIZE = serverToClients(new Identifier("minebp", "server_other_player_size"), MsgOtherPlayerSize::new);

    static void bootstrap() { }

    static <T extends Packet> SPacketType<T> clientToServer(Identifier id, Function<PacketByteBuf, T> factory) {
        ServerPlayNetworking.registerGlobalReceiver(id, (server, player, ignored, buffer, responder) -> {
            T packet = factory.apply(buffer);
            server.execute(() -> packet.handle(player));
        });
        return packet -> {
            if (FabricLoader.getInstance().getEnvironmentType() != EnvType.CLIENT) {
                throw new RuntimeException("Client packet send called by the server");
            }
            ClientPlayNetworking.send(id, toBuffer(packet));
        };
    }

    static <T extends Packet> CPacketType<T> serverToClient(Identifier id, Function<PacketByteBuf, T> factory) {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            ClientPlayNetworking.registerGlobalReceiver(id, (client, ignore1, buffer, ignore2) -> {
                T packet = factory.apply(buffer);
                client.execute(() -> packet.handle(client.player));
            });
        }
        return (recipient, packet) -> {
            ServerPlayNetworking.send((ServerPlayerEntity)recipient, id, toBuffer(packet));
        };
    }

    static <T extends Packet> MPacketType<T> serverToClients(Identifier id, Function<PacketByteBuf, T> factory) {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            ClientPlayNetworking.registerGlobalReceiver(id, (client, ignore1, buffer, ignore2) -> {
                T packet = factory.apply(buffer);
                client.execute(() -> packet.handle(client.player));
            });
        }
        return (world, packet) -> {
            world.getPlayers().forEach(player -> {
                if (player != null) {
                    ServerPlayNetworking.send((ServerPlayerEntity)player, id, toBuffer(packet));
                }
            });
        };
    }

    interface MPacketType<T extends Packet> {
        void send(World world, T packet);
    }

    interface CPacketType<T extends Packet> {
        void send(PlayerEntity recipient, T packet);
    }

    interface SPacketType<T extends Packet> {
        void send(T packet);
    }

    static PacketByteBuf toBuffer(Packet packet) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        packet.toBuffer(buf);
        return buf;
    }

    interface Packet {
        void toBuffer(PacketByteBuf buffer);

        void handle(PlayerEntity sender);
    }
}
