package io.github.hiveko.vanillafinest;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

/** Minecraft 26.1.2, 26.2 and 26.3 (unobfuscated). */
public final class Platform {
    private Platform() {}

    /** Unbound key in the category "key.category.<namespace>.<path>". */
    public static KeyMapping registerKey(String name, String namespace, String path) {
        return KeyMappingHelper.registerKeyMapping(new KeyMapping(name, InputConstants.UNKNOWN.getValue(),
                new KeyMapping.Category(Identifier.fromNamespaceAndPath(namespace, path))));
    }

    public static void showOverlay(Minecraft client, Component message) {
        client.player.sendOverlayMessage(message);
    }
}
