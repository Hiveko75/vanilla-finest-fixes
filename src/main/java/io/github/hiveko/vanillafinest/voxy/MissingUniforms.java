package io.github.hiveko.vanillafinest.voxy;

import io.github.hiveko.vanillafinest.VanillaFinest;
import org.slf4j.Logger;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * Lets Voxy compile a shader pack's distant-terrain shader when the pack uses a value that Iris does not
 * provide on this Minecraft version (mixin.voxy, 26.2 and 26.3).
 *
 * A shader pack lists the uniforms its Voxy shader needs in its {@code voxy.json}. Voxy builds them from
 * what Iris has, and silently leaves out any that Iris does not know. Iris programs still compile in that
 * case, because packs give such uniforms a default in their own code (Complementary r5.10:
 * {@code uniform float inIceCaves = 0.0;}, a biome that only exists from Minecraft 26.4), but Voxy's shader
 * does not include that code, so it fails with "undefined variable" and Voxy falls back to drawing
 * distant terrain without the shader pack, with an error in chat.
 *
 * Two kinds of names are checked: every uniform the pack asked for that Voxy left out, and a short list
 * of uniforms packs are known to use without asking for them ({@link #KNOWN_DEFAULTS}: Complementary
 * r5.10 only lists {@code inIceCaves} in its voxy.json for 26.4+, but its volumetric light code uses it
 * from 26.2 on). When the shader uses such a name and declares it nowhere, a
 * {@code const float NAME = <default>;} is added in front of the pack's code: the value the pack itself
 * uses when Iris does not set it (0 unless listed otherwise). A uniform of another type would still fail
 * as before; a shader that already compiles never gets anything added.
 */
public final class MissingUniforms {
    private static final Logger LOGGER = VanillaFinest.LOGGER;

    /** Uniforms packs use in their Voxy code without listing them, with the pack's own default. */
    private static final Map<String, String> KNOWN_DEFAULTS = Map.of(
            "inIceCaves", "0.0"   // Complementary r5.10 lib/uniforms.glsl: uniform float inIceCaves = 0.0;
    );

    /** The uniform list of the shader pack's Voxy patch, captured when Voxy reads it. */
    public static volatile String[] requested;

    private static final Set<String> LOGGED = ConcurrentHashMap.newKeySet();
    private static final Set<Integer> CHECKED = ConcurrentHashMap.newKeySet();
    private static final Pattern IDENTIFIER = Pattern.compile("[A-Za-z_][A-Za-z0-9_]*");
    private static volatile boolean disabled;

    private MissingUniforms() {}

    /**
     * @param pipelineData Voxy's IrisVoxyRenderPipelineData
     * @param source       the opaque or translucent patch source it returns
     * @return the source with declarations added, or null to keep it unchanged
     */
    public static String patch(Object pipelineData, String source) {
        if (disabled || source == null) return null;
        Set<String> names = new LinkedHashSet<>();
        String[] listed = requested;
        if (listed != null) names.addAll(Arrays.asList(listed));
        names.addAll(KNOWN_DEFAULTS.keySet());
        try {
            Object layout = pipelineData.getClass().getMethod("getUniforms").invoke(pipelineData);
            String block = layout == null ? "" : String.valueOf(layout.getClass().getMethod("layout").invoke(layout));
            StringBuilder declarations = new StringBuilder();
            Set<String> added = new TreeSet<>();
            for (String name : names) {
                if (name == null || !IDENTIFIER.matcher(name).matches()) continue;
                if (inBlock(block, name) || !uses(source, name) || declares(source, name)) continue;
                declarations.append("const float ").append(name).append(" = ")
                        .append(KNOWN_DEFAULTS.getOrDefault(name, "0.0")).append(";\n");
                added.add(name);
            }
            if (added.isEmpty()) {
                if (CHECKED.add(source.length())) {
                    LOGGER.info("Voxy shader fix: nothing to add ({} uniforms listed by the pack; inIceCaves used: {}, in Voxy's uniforms: {}, declared: {})",
                            listed == null ? "no" : listed.length, uses(source, "inIceCaves"), inBlock(block, "inIceCaves"), declares(source, "inIceCaves"));
                }
                return null;
            }
            String key = String.join(",", added);
            if (LOGGED.add(key)) {
                LOGGER.info("Voxy shader fix: the shader pack uses {}, which Iris does not provide on this Minecraft version; set to 0 (the pack's own default) so Voxy can compile it", key);
            }
            return declarations + source;
        } catch (ReflectiveOperationException | RuntimeException | LinkageError e) {
            disabled = true;
            LOGGER.warn("Voxy shader uniform fix turned off: this Voxy version is not compatible", e);
            return null;
        }
    }

    private static boolean inBlock(String block, String name) {
        return Pattern.compile("\\b" + name + "\\s*;").matcher(block).find();
    }

    private static boolean uses(String source, String name) {
        return Pattern.compile("(?<![\\w.])" + name + "\\b").matcher(source).find();
    }

    private static boolean declares(String source, String name) {
        return Pattern.compile("\\b(?:float|double|int|uint|bool|[iubd]?vec[234]|d?mat[234](?:x[234])?)\\s+" + name + "\\b")
                .matcher(source).find();
    }
}
