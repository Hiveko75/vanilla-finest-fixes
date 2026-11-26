package io.github.hiveko.vanillafinest.fixes.gnetum;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;

/**
 * Gnetum module: Gnetum 4.6.5 builds its page in Sodium's Video Settings once, when the game has loaded, and
 * turns its HUD element names (and their tooltips) into plain text then. A language chosen later, like the
 * one the setup screen adds on a first start, or a resource pack added later, never reached them until a
 * restart. {@code mixin.gnetum.GnetumSodiumPageMixin} gives Sodium texts that are looked up each time they are
 * drawn instead, made here. Gnetum's own keys are used: {@code gnetum.config.element.<id>} (":" as "."), and
 * {@code gnetum.config.element.<id>.tooltip}.
 */
public final class GnetumTexts {
    private GnetumTexts() {}

    /** The element's name in the current language; Gnetum's own English name made from the id if there is none. */
    public static Component name(String id) {
        if (id.equals("_unknown_")) return Component.translatable("gnetum.config.unknown_element");
        if (id.equals("_unknown_listeners_")) return Component.translatable("gnetum.config.unknown_listeners");
        return Component.translatableWithFallback("gnetum.config.element." + id.replace(':', '.'), englishName(id));
    }

    /** The element's tooltip in the current language, or null. */
    public static String tooltip(String id) {
        return Language.getInstance().getOrDefault("gnetum.config.element." + id + ".tooltip", null);
    }

    /** A page name or value text of ours ({@code vanilla-finest-fixes.gnetum.<name>}), or Gnetum's English. */
    public static Component text(String name, String english, Object... args) {
        return Component.translatableWithFallback("vanilla-finest-fixes.gnetum." + name, english, args);
    }

    /**
     * Gnetum's name for an element without a translation (its {@code Beautifier}): "misc_overlays" →
     * "Misc Overlays" (words of up to two letters and "and" stay as they are), "simple_music_control:info_overlay"
     * → "[Simple Music Control] Info Overlay" (the mod's name when it is installed, else its id).
     */
    static String englishName(String id) {
        int colon = id.indexOf(':');
        if (colon < 0) {
            StringBuilder out = new StringBuilder();
            for (String word : id.split("_")) {
                if (!out.isEmpty()) out.append(' ');
                out.append(word.length() <= 2 || word.equals("and") ? word
                        : Character.toUpperCase(word.charAt(0)) + word.substring(1));
            }
            return out.toString();
        }
        String namespace = id.substring(0, colon);
        String path = id.substring(colon + 1);
        String mod = FabricLoader.getInstance().getModContainer(namespace)
                .map(c -> c.getMetadata().getName()).orElse(namespace);
        if (!path.isEmpty()) path = Character.toUpperCase(path.charAt(0)) + path.substring(1);
        for (int i = path.indexOf('_'); i >= 0 && i + 1 < path.length(); i = path.indexOf('_')) {
            path = path.substring(0, i) + " " + Character.toUpperCase(path.charAt(i + 1)) + path.substring(i + 2);
        }
        return "[" + mod + "] " + path;
    }
}
