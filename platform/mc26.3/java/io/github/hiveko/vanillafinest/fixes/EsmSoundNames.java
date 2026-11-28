package io.github.hiveko.vanillafinest.fixes;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.client.Minecraft;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.Identifier;

/**
 * Readable names for the sounds in Extreme Sound Muffler's lists
 * ({@link io.github.hiveko.vanillafinest.fixes.mixin.esm.EsmSliderMixin}), in the game's language; the sound id
 * stays in the row's tooltip. In this order:
 * <ol>
 *   <li>our own name, {@code vanilla-finest-fixes.esm.sound.<namespace>.<path>} (sounds without a subtitle);</li>
 *   <li>names built from the game's own texts: music ("Music – Lush Caves", from the biome name), music discs
 *       (the disc's text), the Nether biomes' ambient loops, and AmbientSounds' numbered sounds
 *       ({@code vanilla-finest-fixes.esm.sound.ambientsounds.<path without the number>} + the number);</li>
 *   <li>the sound's subtitle (what the game shows with subtitles on). When other sounds share that subtitle, the
 *       part of the id that tells them apart is added: "Footsteps – Grass" (a block, item or mob of that name, or
 *       {@code vanilla-finest-fixes.esm.part.<word>}).</li>
 * </ol>
 * Anything else keeps its id. Only the shown text changes: ESM keeps the ids, so muffled sounds stay muffled.
 */
public final class EsmSoundNames {
    private static final String P = "vanilla-finest-fixes.esm.";
    private static final Pattern NUMBERED = Pattern.compile("^(.*?)(\\d+)$");
    private static Map<String, Integer> subtitleUses;
    private static SoundManager countedFor;

    private EsmSoundNames() {
    }

    /** A readable name for a sound id ("minecraft:entity.enderman.scream"), or null to keep the id. */
    public static String name(String id) {
        Identifier sound = Identifier.tryParse(id);
        if (sound == null || id.indexOf(':') < 0) return null;
        Language lang = Language.getInstance();
        String ns = sound.getNamespace();
        String path = sound.getPath();

        String own = P + "sound." + ns + "." + path;
        if (lang.has(own)) return lang.getOrDefault(own, id);

        String built = built(lang, ns, path);
        if (built != null) return built;

        SoundManager manager = Minecraft.getInstance().getSoundManager();
        WeighedSoundEvents event = manager.getSoundEvent(sound);
        Component subtitle = event == null ? null : event.getSubtitle();
        if (subtitle == null) return null;
        String text = subtitle.getString();
        if (text.isBlank()) return null;
        String key = subtitle.getContents() instanceof TranslatableContents t ? t.getKey() : null;
        if (key != null && uses(manager, key) > 1) {
            String part = part(lang, ns, path, key);
            if (part != null) return lang.getOrDefault(P + "with", "%s – %s").formatted(text, part);
        }
        return text;
    }

    private static String built(Language lang, String ns, String path) {
        if (ns.equals("minecraft")) {
            if (path.startsWith("music_disc.")) {
                String disc = "item.minecraft.music_disc_" + path.substring("music_disc.".length()) + ".desc";
                if (lang.has(disc)) return fmt(lang, "music_disc", lang.getOrDefault(disc, disc));
            }
            String[] parts = path.split("\\.");
            if (parts.length == 3 && parts[0].equals("music") && (parts[1].equals("overworld") || parts[1].equals("nether"))) {
                String biome = "biome.minecraft." + parts[2];
                if (lang.has(biome)) return fmt(lang, "music", lang.getOrDefault(biome, biome));
            }
            if (parts.length == 3 && parts[0].equals("ambient") && lang.has("biome.minecraft." + parts[1])) {
                String kind = lang.getOrDefault(P + "ambient." + parts[2], parts[2]);
                return fmt(lang, "ambient", lang.getOrDefault("biome.minecraft." + parts[1], parts[1])) + " (" + kind + ")";
            }
        }
        if (ns.equals("ambientsounds")) {
            Matcher m = NUMBERED.matcher(path);
            String base = m.matches() ? m.group(1) : path;
            String number = m.matches() ? " " + m.group(2) : "";
            String key = P + "sound.ambientsounds." + (base.endsWith("-") || base.endsWith("_") ? base.substring(0, base.length() - 1) : base);
            if (lang.has(key)) return lang.getOrDefault(key, key) + number;
        }
        return null;
    }

    private static String fmt(Language lang, String what, String value) {
        return lang.getOrDefault(P + what, "%s").formatted(value);
    }

    /** The part of the id that differs from the subtitle's key, as a name ("grass" → "Grass"). */
    private static String part(Language lang, String ns, String path, String subtitleKey) {
        String[] id = path.split("\\.");
        String[] sub = (subtitleKey.startsWith("subtitles.") ? subtitleKey.substring(10) : subtitleKey).split("\\.");
        int i = 0;
        while (i < id.length && (id[i].equals("pf_presence") || (i < sub.length && id[i].equals(sub[i])))) i++;
        if (i >= id.length) return null;
        String word = id[i];
        if (word.equals("sound") && i + 1 < id.length) word = word + "." + id[i + 1];
        String ownKey = P + "part." + word;
        if (lang.has(ownKey)) return lang.getOrDefault(ownKey, word);
        String home = ns.startsWith("presencefootsteps") ? "minecraft" : ns;
        for (String type : new String[] {"block.", "item.", "entity."}) {
            for (String w : new String[] {word, word + "_block"}) {
                String key = type + home + "." + w;
                if (lang.has(key)) return lang.getOrDefault(key, w);
            }
        }
        return word.replace('_', ' ');
    }

    /** How many sounds use this subtitle key (counted once per sound reload). */
    private static synchronized int uses(SoundManager manager, String key) {
        if (subtitleUses == null || countedFor != manager) {
            Map<String, Integer> counts = new HashMap<>();
            for (Identifier sound : manager.getAvailableSounds()) {
                WeighedSoundEvents event = manager.getSoundEvent(sound);
                Component subtitle = event == null ? null : event.getSubtitle();
                if (subtitle != null && subtitle.getContents() instanceof TranslatableContents t) {
                    counts.merge(t.getKey(), 1, Integer::sum);
                }
            }
            subtitleUses = counts;
            countedFor = manager;
        }
        return subtitleUses.getOrDefault(key, 0);
    }
}
