package com.minelittlepony.bigpony.hdskins;

import java.util.concurrent.CompletableFuture;

import com.minelittlepony.bigpony.util.FutureUtils;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.util.Identifier;

public class SkinDetecter {
    static SkinDetecter INSTANCE = new SkinDetecter();

    public static SkinDetecter getInstance() {
        return INSTANCE;
    }

    SkinDetecter() {}

    public CompletableFuture<Identifier> loadSkin(GameProfile profile) {
        return FutureUtils.<Identifier>waitFor(callback -> {
            MinecraftClient.getInstance().getSkinProvider().loadSkin(profile, (type, id, texture) -> {
                if (type == MinecraftProfileTexture.Type.SKIN) {
                    callback.accept(id);
                }
            }, false);
        }, () -> DefaultSkinHelper.getTexture(profile.getId()));
    }
}
