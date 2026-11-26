package io.github.hiveko.vanillafinest.fixes.clouds;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Better Clouds 1.14 (Minecraft 26.2) preset list repair, used by {@code mixin.betterclouds.PresetListMixin}.
 *
 * Every time Better Clouds 1.14 loads its built-in presets it keeps the ones already in the config, adds the
 * built-in ones again and re-sorts the list. The built-in "Default" counts as editable, so it is never removed
 * first: each start added one more "Default" (one instance had 13). The re-sort keeps only the selected
 * position number, not the preset, so the selection moved to another preset; that is how "Mellow (auto)",
 * chosen by {@link MellowPreset}, turned back into a "Default". Better Clouds 1.15 removes presets whose key
 * is already in the list; this does the same and then selects the preset that was selected before.
 * Presets are reached by name (their public {@code key} field), nothing is compiled against Better Clouds.
 */
public final class BetterCloudsPresets {
    private static final Logger LOGGER = LoggerFactory.getLogger("Vanilla Finest Fixes");

    private BetterCloudsPresets() {}

    /** The preset at the selected position (clamped as Better Clouds does), or null for an empty list. */
    public static Object selected(List<?> list, int index) {
        if (list == null || list.isEmpty()) return null;
        return list.get(Math.clamp(index, 0, list.size() - 1));
    }

    /**
     * Removes every preset whose key appeared earlier in the list and returns the position of {@code before}
     * (the preset selected before Better Clouds changed the list), of the kept preset with its key if it was a
     * removed copy, or {@code fallback} clamped to the list.
     */
    public static int dedupeAndFind(List<?> list, Object before, int fallback, String what) {
        if (list == null || list.isEmpty()) return 0;
        String beforeKey = key(before);
        Set<String> seen = new HashSet<>();
        int size = list.size();
        list.removeIf(preset -> {
            String key = key(preset);
            return key != null && !seen.add(key);
        });
        int removed = size - list.size();
        if (removed > 0) {
            LOGGER.info("Better Clouds: removed {} duplicate {} preset(s)", removed, what);
        }
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) == before) return i;
        }
        if (beforeKey != null) {
            for (int i = 0; i < list.size(); i++) {
                if (beforeKey.equals(key(list.get(i)))) return i;
            }
        }
        return Math.clamp(fallback, 0, list.size() - 1);
    }

    private static String key(Object preset) {
        if (preset == null) return null;
        try {
            Field field = preset.getClass().getField("key");
            return field.get(preset) instanceof String key ? key : null;
        } catch (ReflectiveOperationException | RuntimeException e) {
            return null;
        }
    }
}
