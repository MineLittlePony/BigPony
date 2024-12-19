package com.minelittlepony.bigpony.hdskins;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import com.minelittlepony.bigpony.util.FutureUtils;
import com.minelittlepony.hdskins.client.HDSkins;
import com.minelittlepony.hdskins.profile.SkinType;
import com.mojang.authlib.GameProfile;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.util.Identifier;

public class Main extends SkinDetecter implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        INSTANCE = this;
    }

    @Override
    public CompletableFuture<Identifier> loadSkin(GameProfile profile) {
        return FutureUtils.<Optional<Identifier>>waitFor(callback -> {
            HDSkins.getInstance().getProfileRepository().fetchSkins(profile, (type, id, texture) -> {
                if (type == SkinType.SKIN) {
                    callback.accept(Optional.of(id));
                }
            });
        }, Optional::empty).thenCompose(value -> value.map(CompletableFuture::completedFuture).orElseGet(() -> super.loadSkin(profile)));
    }
}
