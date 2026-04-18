package com.minelittlepony.bigpony.hdskins;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import com.minelittlepony.bigpony.util.FutureUtils;
import com.mojang.authlib.GameProfile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.core.ClientAsset;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.PlayerSkin;

public class SkinDetecter {
    static SkinDetecter INSTANCE = new SkinDetecter();

    public static SkinDetecter getInstance() {
        return INSTANCE;
    }

    SkinDetecter() {}

    public CompletableFuture<Identifier> loadSkin(GameProfile profile) {
        return FutureUtils.either(
                Minecraft.getInstance().getSkinManager().get(profile),
                Optional::empty
        ).thenApply(result -> result.orElseGet(() -> DefaultPlayerSkin.get(profile.id())))
         .thenApply(PlayerSkin::body)
         .thenApply(ClientAsset.Texture::texturePath);
    }
}
