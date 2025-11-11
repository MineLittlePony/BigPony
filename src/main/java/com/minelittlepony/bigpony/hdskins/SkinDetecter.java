package com.minelittlepony.bigpony.hdskins;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import com.minelittlepony.bigpony.util.FutureUtils;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.entity.player.SkinTextures;
import net.minecraft.util.AssetInfo;
import net.minecraft.util.Identifier;

public class SkinDetecter {
    static SkinDetecter INSTANCE = new SkinDetecter();

    public static SkinDetecter getInstance() {
        return INSTANCE;
    }

    SkinDetecter() {}

    public CompletableFuture<Identifier> loadSkin(GameProfile profile) {
        return FutureUtils.either(
                MinecraftClient.getInstance().getSkinProvider().fetchSkinTextures(profile),
                Optional::empty
        ).thenApply(result -> result.orElseGet(() -> DefaultSkinHelper.getSkinTextures(profile.id())))
         .thenApply(SkinTextures::body)
         .thenApply(AssetInfo.TextureAsset::texturePath);
    }
}
