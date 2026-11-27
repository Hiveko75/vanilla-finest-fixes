package io.github.hiveko.vanillafinest.fixes;

import net.minecraft.locale.Language;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** See {@link io.github.hiveko.vanillafinest.fixes.mixin.iris.IrisLanguageMixin}. */
public final class IrisLanguageFix {
    private static final Logger LOGGER = LoggerFactory.getLogger("Vanilla Finest Fixes");
    private static boolean logged;

    private IrisLanguageFix() {
    }

    /**
     * Reads iris:lang/&lt;code&gt;.json again for every language, lowest pack first, as Minecraft does.
     * Returns false when there was nothing to read (then the caller keeps the original map).
     */
    public static boolean reapply(ResourceManager resourceManager, List<String> languages, Map<String, String> translations) {
        int packs = 0;
        for (String code : languages) {
            Identifier id = Identifier.fromNamespaceAndPath("iris", String.format(Locale.ROOT, "lang/%s.json", code));
            List<Resource> stack;
            try {
                stack = resourceManager.getResourceStack(id);
            } catch (RuntimeException e) {
                continue;
            }
            for (Resource resource : stack) {
                try (InputStream in = resource.open()) {
                    Language.loadFromJson(in, translations::put);
                    packs++;
                } catch (Exception e) {
                    LOGGER.warn("Could not read {} from {}: {}", id, resource.sourcePackId(), e.toString());
                }
            }
        }
        if (!logged && packs > 0) {
            logged = true;
            LOGGER.info("Iris texts: resource packs win over Iris's own language file again ({} files read)", packs);
        }
        return packs > 0;
    }
}
