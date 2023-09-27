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
        return FutureUtils.<Optional<Identifier>>either(
            HDSkins.getInstance().getProfileRepository().load(profile).thenApply(skins -> {
                return skins.getSkin(SkinType.SKIN);
            }),
            Optional::empty
        ).thenCompose(value -> value.map(CompletableFuture::completedFuture).orElseGet(() -> super.loadSkin(profile)));
    }
}
