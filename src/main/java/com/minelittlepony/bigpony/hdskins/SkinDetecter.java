package com.minelittlepony.bigpony.hdskins;

import java.util.concurrent.CompletableFuture;

import com.minelittlepony.bigpony.util.FutureUtils;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.MinecraftClient;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.util.Identifier;

public class SkinDetecter {
    static SkinDetecter INSTANCE = new SkinDetecter();

    public static SkinDetecter getInstance() {
        return INSTANCE;
    }

    SkinDetecter() {}

    public CompletableFuture<Identifier> loadSkin(GameProfile profile) {
        return FutureUtils.waitFor(callback -> {
            MinecraftClient.getInstance().getSkinProvider().loadSkin(profile, (type, texture, payload) -> {
                if (type == MinecraftProfileTexture.Type.SKIN) {
                    MinecraftClient.getInstance().executeTask(() -> callback.accept(texture));
                }
            }, false);
        }, () -> DefaultSkinHelper.getTexture(profile.getId()));
    }
}
