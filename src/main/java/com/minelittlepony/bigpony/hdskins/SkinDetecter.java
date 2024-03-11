package com.minelittlepony.bigpony.hdskins;

import java.util.concurrent.CompletableFuture;

import com.minelittlepony.bigpony.util.FutureUtils;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.client.util.SkinTextures;
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
                () -> DefaultSkinHelper.getSkinTextures(profile.getId())
        ).thenApply(SkinTextures::texture);
    }
}
