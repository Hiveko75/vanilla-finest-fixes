package io.github.hiveko.vanillafinest.voxy;

import io.github.hiveko.vanillafinest.VanillaFinest;
import org.slf4j.Logger;

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
 * For every uniform the pack asked for that Voxy left out, and that the shader uses without declaring
 * it, a {@code const float NAME = 0.0;} is added in front of the pack's code: the value the pack itself
 * uses when Iris does not set it. A uniform of another type would still fail as before; a shader that
 * already compiles never gets anything added.
 */
public final class MissingUniforms {
    private static final Logger LOGGER = VanillaFinest.LOGGER;

    /** The uniform list of the shader pack's Voxy patch, captured when Voxy reads it. */
    public static volatile String[] requested;

    private static final Set<String> LOGGED = ConcurrentHashMap.newKeySet();
    private static final Pattern IDENTIFIER = Pattern.compile("[A-Za-z_][A-Za-z0-9_]*");
    private static volatile boolean disabled;

    private MissingUniforms() {}

    /**
     * @param pipelineData Voxy's IrisVoxyRenderPipelineData
     * @param source       the opaque or translucent patch source it returns
     * @return the source with declarations added, or null to keep it unchanged
     */
    public static String patch(Object pipelineData, String source) {
        String[] names = requested;
        if (disabled || source == null || names == null || names.length == 0) return null;
        try {
            Object layout = pipelineData.getClass().getMethod("getUniforms").invoke(pipelineData);
            String block = layout == null ? "" : String.valueOf(layout.getClass().getMethod("layout").invoke(layout));
            StringBuilder declarations = new StringBuilder();
            Set<String> added = new TreeSet<>();
            for (String name : names) {
                if (name == null || !IDENTIFIER.matcher(name).matches()) continue;
                if (inBlock(block, name) || !uses(source, name) || declares(source, name)) continue;
                declarations.append("const float ").append(name).append(" = 0.0;\n");
                added.add(name);
            }
            if (added.isEmpty()) return null;
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
