package io.github.hiveko.vanillafinest.fixes;

import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.locale.Language;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.block.Block;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Block names for Extreme Sound Muffler's Blocks list ({@link io.github.hiveko.vanillafinest.fixes.mixin.esm.EsmSliderMixin}).
 * ESM 4.06 names each block with {@code block.getName().getString()} once, when it first fills its block cache,
 * and saves that cache, so the names stay in the language that was active then (English on a new instance). This
 * maps such an English name back to the block's translation key, read from the en_us language files of the blocks'
 * namespaces (built once, on first use), and gives the name in the current language.
 */
public final class EsmBlockNames {
    private static final Logger LOGGER = LoggerFactory.getLogger("Vanilla Finest Fixes");
    private static Map<String, String> englishToKey;

    private EsmBlockNames() {
    }

    /** The block's name in the game's language when {@code text} is a block's English name, else {@code text}. */
    public static String shown(String text) {
        if (text == null || text.isEmpty() || text.indexOf(':') >= 0) return text;
        String key = map().get(text);
        return key == null ? text : Language.getInstance().getOrDefault(key, text);
    }

    private static synchronized Map<String, String> map() {
        if (englishToKey != null) return englishToKey;
        Map<String, String> english = new HashMap<>();
        Map<String, String> out = new HashMap<>();
        try {
            ResourceManager resources = Minecraft.getInstance().getResourceManager();
            Set<String> namespaces = new HashSet<>();
            for (Identifier id : BuiltInRegistries.BLOCK.keySet()) namespaces.add(id.getNamespace());
            for (String ns : namespaces) {
                for (Resource resource : resources.getResourceStack(Identifier.fromNamespaceAndPath(ns, "lang/en_us.json"))) {
                    try (InputStream in = resource.open()) {
                        Language.loadFromJson(in, english::put);
                    } catch (Exception e) {
                        // a broken language file: its names stay as ESM saved them
                    }
                }
            }
            for (Block block : BuiltInRegistries.BLOCK) {
                String key = block.getDescriptionId();
                String name = english.get(key);
                if (name != null) out.putIfAbsent(name, key);
            }
            LOGGER.info("Extreme Sound Muffler: block names follow the game's language ({} blocks)", out.size());
        } catch (RuntimeException e) {
            LOGGER.warn("Extreme Sound Muffler: block names stay as saved ({})", e.toString());
        }
        englishToKey = out;
        return out;
    }
}
