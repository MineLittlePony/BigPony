package com.minelittlepony.bigpony;

import com.minelittlepony.common.client.IModUtilities;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;

/**
 * Ah' am a big pony!
 */
public class FabMod implements ClientModInitializer, IModUtilities {
    @Override
    public void onInitializeClient() {
        BigPony bp = new BigPony(this);
        ClientTickCallback.EVENT.register(bp::onClientTick);
    }

    @Override
    public KeyBinding registerKeybind(String category, int key, String bindName) {
        // normalize Fabric's behavior
        if (bindName.startsWith("key.")) {
            bindName = bindName.replace("key.", "");
        }

        FabricKeyBinding binding = FabricKeyBinding.Builder.create(new Identifier(bindName) {
            @Override
            public String toString() { return getPath(); }
        }, InputUtil.Type.KEYSYM, key, category).build();

        KeyBindingRegistry.INSTANCE.register(binding);
        return binding;
    }
}
