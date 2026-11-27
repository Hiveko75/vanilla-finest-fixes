package io.github.hiveko.vanillafinest.fixes.mixin.iris;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.hiveko.vanillafinest.fixes.IrisLanguageFix;
import net.minecraft.client.resources.language.ClientLanguage;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Iris 1.11.7 copies its own language file into the translations at the start of every
 * {@code appendFrom} call, that is once for every mod (namespace) that has a file for the language.
 * So whatever a resource pack sets for Iris's texts is overwritten again by the next mod, and Iris's
 * built-in texts always win (Hungarian: "Be-" / "Ki-" on every shader on/off button). When the
 * finished translations are copied into the new language, this reads the {@code iris} namespace's
 * files once more in the normal order (Iris's own file first, resource packs on top), so resource
 * packs win as they do for every other mod. Iris's keys all contain "iris", so no other mod's text is
 * touched. Hooks the {@code Map.copyOf} at the end of {@code loadFrom}, not the deprecated-key call,
 * which Language Reload already wraps.
 */
@Mixin(ClientLanguage.class)
public abstract class IrisLanguageMixin {
    @ModifyExpressionValue(method = "loadFrom", at = @At(value = "INVOKE",
            target = "Ljava/util/Map;copyOf(Ljava/util/Map;)Ljava/util/Map;"))
    private static Map<String, String> vanillaFinest$irisTextsFromResourcePacks(Map<String, String> translations,
                                                                                @Local(argsOnly = true) ResourceManager resourceManager,
                                                                                @Local(argsOnly = true) List<String> languages) {
        Map<String, String> fixed = new HashMap<>(translations);
        if (!IrisLanguageFix.reapply(resourceManager, languages, fixed)) return translations;
        return Map.copyOf(fixed);
    }
}
