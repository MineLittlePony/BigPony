package com.minelittlepony.bigpony.hdskins;

import java.util.concurrent.CompletableFuture;

import com.minelittlepony.bigpony.util.FutureUtils;
import com.minelittlepony.hdskins.client.HDSkins;
import com.minelittlepony.hdskins.profile.SkinType;
import com.mojang.authlib.GameProfile;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

public class Main extends SkinDetecter implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        INSTANCE = this;
    }

    @Override
    public CompletableFuture<Identifier> loadSkin(GameProfile profile) {
        return FutureUtils.<Identifier>waitFor(callback -> {
            HDSkins.getInstance().getProfileRepository().fetchSkins(profile, (type, texture, payload) -> {
                if (type == SkinType.SKIN) {
                    MinecraftClient.getInstance().executeTask(() -> callback.accept(texture));
                }
            });
        }, () -> null).thenCompose(value -> value == null ? super.loadSkin(profile) : CompletableFuture.completedFuture(value));
    }
}
