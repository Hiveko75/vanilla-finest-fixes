package io.github.hiveko.vanillafinest.fixes;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

/** Minecraft 1.21.11 (obfuscated, built with Mojang mappings and remapped). */
public final class Platform {
    private Platform() {}

    /** Unbound key in the category "key.category.<namespace>.<path>". */
    public static KeyMapping registerKey(String name, String namespace, String path) {
        return KeyBindingHelper.registerKeyBinding(new KeyMapping(name, InputConstants.UNKNOWN.getValue(),
                new KeyMapping.Category(Identifier.fromNamespaceAndPath(namespace, path))));
    }

    public static void showOverlay(Minecraft client, Component message) {
        client.player.displayClientMessage(message, true);
    }

    /** Partial tick of the frame being rendered (for interpolated view directions). */
    public static float partialTick(Minecraft client) {
        return client.getDeltaTracker().getGameTimeDeltaPartialTick(false);
    }
}
